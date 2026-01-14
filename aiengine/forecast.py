from prophet import Prophet
import pandas as pd

FIXED_CATEGORIES = ["Rent", "EMI", "Netflix", "Prime", "Internet", "Mobile", "Insurance"]

def predict_next_6_months(transactions):
    try:
        # Defensive check: ensure input is a DataFrame
        if not isinstance(transactions, pd.DataFrame):
            raise ValueError("Input transactions must be a pandas DataFrame.")
        
        # Return 0 if empty
        if transactions.empty:
            return 0.0

        # Ensure expected columns exist
        expected_cols = {"amount", "category", "created_at"}
        if not expected_cols.issubset(transactions.columns):
            raise ValueError(f"Transactions missing required columns: {expected_cols - set(transactions.columns)}")

        # Convert to proper types
        transactions["created_at"] = pd.to_datetime(transactions["created_at"], errors="coerce")
        transactions["amount"] = pd.to_numeric(transactions["amount"], errors="coerce").fillna(0)
        transactions["category"] = transactions["category"].astype(str)

        # Classify fixed vs variable
        transactions["type"] = transactions["category"].apply(
            lambda x: "FIXED" if x in FIXED_CATEGORIES else "VARIABLE"
        )

        # Fixed monthly average
        fixed_monthly = (
            transactions[transactions["type"] == "FIXED"]
            .groupby(pd.Grouper(key="created_at", freq="M"))["amount"]
            .sum()
            .mean()
        )
        fixed_monthly = fixed_monthly if fixed_monthly is not None else 0

        # Variable transactions
        variable = transactions[transactions["type"] == "VARIABLE"]

        if variable.empty:
            return float(fixed_monthly * 6)

        monthly = variable.groupby(
            pd.Grouper(key="created_at", freq="M")
        )["amount"].sum().reset_index()
        monthly.columns = ["ds", "y"]

        if len(monthly) < 2:
            return float((fixed_monthly + monthly["y"].mean()) * 6)

        # Prophet model
        model = Prophet()
        model.fit(monthly)

        future = model.make_future_dataframe(periods=6, freq="M")
        forecast = model.predict(future)

        variable_future = forecast.tail(6)["yhat"].sum()
        total = (fixed_monthly * 6) + variable_future

        return float(total)

    except Exception as e:
        print(f"Error in predict_next_6_months: {e}")
        # Safe fallback: return total fixed expenses for 6 months
        return float(fixed_monthly * 6 if 'fixed_monthly' in locals() else 0)
