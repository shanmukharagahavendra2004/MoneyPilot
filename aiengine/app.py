from flask import Flask, request, jsonify
from sqlalchemy import create_engine, text
import pandas as pd
import re
from prophet import Prophet
import openai
import logging

# ---------------- App Setup ----------------
app = Flask(__name__)
logging.basicConfig(level=logging.INFO)

# ---------------- Database Setup ----------------
DB_URL = "postgresql://postgres:123@localhost:5432/financetracker"
engine = create_engine(DB_URL)

FIXED_CATEGORIES = ["Rent", "EMI", "Netflix", "Prime", "Internet", "Mobile", "Insurance"]

# ---------------- OpenAI Setup ----------------
openai.api_key = "YOUR_OPENAI_API_KEY"

# ---------------- Utility Functions ----------------

def get_salary_by_user_id(user_id: int) -> float:
    try:
        if not isinstance(user_id, int) or user_id <= 0:
            raise ValueError("Invalid user_id")
        query = text("SELECT salary FROM users WHERE id = :id")
        with engine.connect() as conn:
            result = conn.execute(query, {"id": user_id}).fetchone()
            salary = float(result[0]) if result and result[0] else 0.0
            return max(salary, 0)
    except Exception as e:
        logging.error(f"Salary DB error for user_id={user_id}: {e}")
        return 0.0

def get_payments_by_user_id(user_id: int) -> pd.DataFrame:
    try:
        if not isinstance(user_id, int) or user_id <= 0:
            raise ValueError("Invalid user_id")
        query = text("""
            SELECT amount, category, created_at
            FROM payments
            WHERE user_id = :id
        """)
        df = pd.read_sql(query, engine, params={"id": user_id})
        if df.empty:
            return pd.DataFrame(columns=["amount", "category", "created_at"])
        # Defensive: ensure correct types
        df["amount"] = pd.to_numeric(df["amount"], errors="coerce").fillna(0)
        df["category"] = df["category"].astype(str)
        df["created_at"] = pd.to_datetime(df["created_at"], errors="coerce")
        return df
    except Exception as e:
        logging.error(f"Payments DB error for user_id={user_id}: {e}")
        return pd.DataFrame(columns=["amount", "category", "created_at"])

def predict_monthly_spend(payments: pd.DataFrame) -> float:
    try:
        if payments.empty:
            return 0.0
        payments = payments.dropna(subset=["created_at"])
        payments["month"] = payments["created_at"].dt.to_period("M")
        monthly = payments.groupby("month")["amount"].sum()
        return float(monthly.mean()) if not monthly.empty else 0.0
    except Exception as e:
        logging.error(f"Monthly spend prediction error: {e}")
        return 0.0

def get_future_savings_6_months(payments: pd.DataFrame, salary: float) -> float:
    try:
        if payments.empty:
            return salary * 6
        payments["type"] = payments["category"].apply(lambda x: "FIXED" if x in FIXED_CATEGORIES else "VARIABLE")
        fixed_monthly = payments[payments["type"]=="FIXED"].groupby(pd.Grouper(key="created_at", freq="M"))["amount"].sum().mean()
        fixed_monthly = fixed_monthly if fixed_monthly else 0
        variable = payments[payments["type"]=="VARIABLE"]
        if variable.empty:
            future_spend = fixed_monthly * 6
        else:
            monthly = variable.groupby(pd.Grouper(key="created_at", freq="M"))["amount"].sum().reset_index()
            monthly.columns = ["ds", "y"]
            if len(monthly) < 2:
                future_spend = fixed_monthly * 6 + monthly["y"].mean() * 6
            else:
                model = Prophet()
                model.fit(monthly)
                future = model.make_future_dataframe(periods=6, freq="M")
                forecast = model.predict(future)
                future_spend = fixed_monthly * 6 + forecast.tail(6)["yhat"].sum()
        future_income = salary * 6
        return future_income - future_spend
    except Exception as e:
        logging.error(f"Future savings prediction error: {e}")
        avg_spend = payments["amount"].mean() if not payments.empty else 0
        return max((salary - avg_spend) * 6, 0)

def cashflow(balance: float, salary: float, future_6m: float):
    try:
        monthly_income = salary
        monthly_expense = (salary*6 - future_6m) / 6
        if monthly_expense > monthly_income:
            burn = monthly_expense - monthly_income
            runway = balance / burn if burn else 999
        else:
            runway = 999
        return round(monthly_income,2), round(monthly_expense,2), round(runway,1)
    except Exception as e:
        logging.error(f"Cashflow error: {e}")
        return 0.0, 0.0, 0.0

def risk_score(salary: float, spend: float, runway: float) -> str:
    try:
        if salary <= 0:
            return "CRITICAL"
        savings_rate = (salary - spend)/salary
        if spend > salary and runway < 3:
            return "CRITICAL"
        if spend > salary:
            return "HIGH"
        if savings_rate < 0.2:
            return "MEDIUM"
        return "LOW"
    except Exception as e:
        logging.error(f"Risk score error: {e}")
        return "UNKNOWN"

def detect_lifestyle_creep(payments: pd.DataFrame) -> str:
    try:
        if len(payments) < 30:
            return "Not enough data"
        payments["week"] = payments["created_at"].dt.isocalendar().week
        weekly = payments.groupby("week")["amount"].sum()
        if len(weekly) < 4:
            return "Stable"
        growth = (weekly.iloc[-1] - weekly.iloc[-4]) / weekly.iloc[-4] if weekly.iloc[-4] else 0
        return "Lifestyle Inflation Detected" if growth > 0.25 else "Stable"
    except Exception as e:
        logging.error(f"Lifestyle creep detection error: {e}")
        return "Stable"

def llm_cfo_advice(data: dict) -> str:
    try:
        required_keys = ["income","expense","runway","risk","categories"]
        for key in required_keys:
            if key not in data:
                raise ValueError(f"Missing key in data: {key}")
        categories = ', '.join(data["categories"]) if isinstance(data["categories"], (list,tuple)) else str(data["categories"])
        prompt = f"""
You are a financial AI assistant (CFO) for a user.
Here is the user's financial data:
- Monthly Income: {data['income']}
- Monthly Expense: {data['expense']}
- Runway (months): {data['runway']}
- Risk Level: {data['risk']}
- Top Expense Categories: {categories}

Give 3 practical recommendations for the user to improve finances.
Keep it short and clear.
"""
        response = openai.ChatCompletion.create(
            model="gpt-4",
            messages=[{"role":"user","content":prompt}],
            temperature=0.7
        )
        return response.choices[0].message.content.strip()
    except Exception as e:
        logging.error(f"LLM CFO error: {e}")
        return "Could not generate CFO advice at this time."

# ---------------- Flask API ----------------
@app.route("/analyze", methods=["POST"])
def analyze():
    try:
        data = request.get_json(force=True)
        user_id = int(data.get("userId", 0))
        question = str(data.get("question","")).lower()
        if user_id <= 0:
            return jsonify({"answer":"Invalid user ID","advice":""}), 400

        salary = get_salary_by_user_id(user_id)
        payments = get_payments_by_user_id(user_id)
        current_month = pd.Timestamp.now().to_period("M")
        this_month = payments[payments["created_at"].dt.to_period("M") == current_month]
        this_month_spend = this_month["amount"].sum() if not this_month.empty else 0
        savings = max(salary - this_month_spend,0)

        monthly_spend = predict_monthly_spend(payments)
        future_6m = get_future_savings_6_months(payments, salary)

        balance = max(salary - payments["amount"].sum(),0)
        monthly_income, monthly_expense, runway = cashflow(balance, salary, future_6m)
        risk = risk_score(monthly_income, monthly_expense, runway)
        lifestyle = detect_lifestyle_creep(payments)
        top_categories = payments.groupby("category")["amount"].sum().sort_values(ascending=False).head(3).index.tolist()

        advice = llm_cfo_advice({
            "income": monthly_income,
            "expense": monthly_expense,
            "runway": runway,
            "risk": risk,
            "categories": top_categories
        })

        # ---------- Question Handling ----------
        answer = "Ask me about spending, savings, risk, or future runway."
        if "spend" in question:
            answer = f"You spend about ₹{int(monthly_spend)} per month."
        elif "save" in question:
            answer = f"You save about ₹{int(savings)} this month."
        elif "afford" in question:
            prices = re.findall(r"\d+", question)
            price = int(prices[0]) if prices else 0
            if price <= savings:
                answer = f"Yes, you can afford this. ₹{int(savings - price)} will remain."
            else:
                answer = f"No, this will leave you short by ₹{int(price - savings)}."
        elif "future" in question or "6" in question:
            answer = f"In 6 months your balance will be around ₹{int(future_6m)}. Runway: {runway} months."
        elif "risk" in question:
            answer = f"Your financial risk level is {risk}."
        elif "why" in question:
            answer = f"Income ₹{salary}, Spend ₹{int(monthly_expense)}, Runway {runway} months. Risk: {risk}. Lifestyle: {lifestyle}."

        return jsonify({"answer": answer, "advice": advice})
    except Exception as e:
        logging.error(f"AI CFO error: {e}")
        return jsonify({"answer": "AI CFO failed to analyze your data.", "advice": ""}), 500

# ---------------- Run App ----------------
if __name__ == "__main__":
    app.run(port=5000, debug=True)
