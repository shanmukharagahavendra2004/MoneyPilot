from flask import Flask, request, jsonify
from sqlalchemy import create_engine, text
import pandas as pd
import re

app = Flask(__name__)

# ---------------- Database Setup ----------------
DB_URL = "postgresql://postgres:123@localhost:5432/financetracker"
engine = create_engine(DB_URL)


# ---------- Load User Salary ----------
def get_salary_by_user_id(user_id: int) -> float:
    print(user_id)
    try:
        query = text("SELECT salary FROM users WHERE id = :id")
        print(query)
        with engine.connect() as conn:
            result = conn.execute(query, {"id": user_id}).fetchone()
            return float(result[0]) if result and result[0] else 0.0
    except Exception as e:
        print("Salary DB error:", e)
        return 0.0


# ---------- Load User Payments ----------
def get_payments_by_user_id(user_id: int) -> pd.DataFrame:
    try:
        query = text("""
            SELECT amount, category, created_at
            FROM payments
            WHERE user_id = :id
        """)
        df = pd.read_sql(query, engine, params={"id": user_id})
        return df if not df.empty else pd.DataFrame(columns=["amount", "category", "created_at"])
    except Exception as e:
        print("Payments DB error:", e)
        return pd.DataFrame(columns=["amount", "category", "created_at"])


# ---------- Predict Monthly Spend ----------
def predict_monthly_spend(payments: pd.DataFrame) -> float:
    try:
        if payments.empty:
            return 0.0

        payments["created_at"] = pd.to_datetime(payments["created_at"], errors="coerce")
        payments = payments.dropna(subset=["created_at"])

        if payments.empty:
            return 0.0

        payments["month"] = payments["created_at"].dt.to_period("M")
        monthly = payments.groupby("month")["amount"].sum()

        return float(monthly.mean()) if not monthly.empty else 0.0
    except Exception as e:
        print("Prediction error:", e)
        return 0.0


# ---------- AI Engine ----------
@app.route("/analyze", methods=["POST"])
def analyze():
    try:
        data = request.get_json(force=True)

        user_id = data.get("userId")
        if user_id is None:
            return jsonify({"answer": "Missing userId"}), 400

        try:
            user_id = int(user_id)
        except:
            return jsonify({"answer": "Invalid userId format"}), 400

        question = data.get("question", "").lower()

        salary = get_salary_by_user_id(user_id)
        payments = get_payments_by_user_id(user_id)
        payments["created_at"] = pd.to_datetime(payments["created_at"], errors="coerce")
        current_month = pd.Timestamp.now().to_period("M")

        this_month = payments[
        payments["created_at"].dt.to_period("M") == current_month]

        this_month_spend = this_month["amount"].sum() if not this_month.empty else 0
        savings = salary - this_month_spend

       

        # --------- AI Reasoning ----------
        if "spend" in question:
            return jsonify({"answer": f"You spend about ₹{int(monthly_spend)} per month."})

        if "save" in question:
            return jsonify({"answer": f"You save about ₹{int(savings)} per month."})

        if "afford" in question:
            prices = re.findall(r"\d+", question)
            price = int(prices[0]) if prices else 0

            if price <= savings:
                return jsonify({
                    "answer": f"Yes — you can afford this. You will still have about ₹{int(savings - price)} left this month."
                })
            else:
                return jsonify({
                    "answer": f"No — this will leave you short by ₹{int(price - savings)} this month."
                })

        if "future" in question or "6 month" in question:
            future = savings * 6
            return jsonify({"answer": f"In 6 months you will save about ₹{int(future)}."})

        return jsonify({"answer": "Ask me about spending, savings, or affordability."})

    except Exception as e:
        print("AI Engine error:", e)
        return jsonify({"answer": "Something went wrong while analyzing your data."}), 500


if __name__ == "__main__":
    app.run(port=5000, debug=True)
