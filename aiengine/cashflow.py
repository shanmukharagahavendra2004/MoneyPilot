import pandas as pd

def cash_flow_forecast(balance, salary, predicted_spend_6m):
    try:
        # ---------------- Defensive checks ----------------
        # Ensure inputs are numbers
        if not all(isinstance(x, (int, float)) for x in [balance, salary, predicted_spend_6m]):
            raise ValueError("All inputs must be numbers (int or float).")

        # Check for negative values (optional depending on business rules)
        if balance < 0 or salary < 0 or predicted_spend_6m < 0:
            raise ValueError("Balance, salary, and predicted spend cannot be negative.")

        # ---------------- Main calculation ----------------
        monthly_income = salary
        monthly_expense = predicted_spend_6m / 6

        runway_months = float("inf")
        if monthly_expense > monthly_income:
            burn = monthly_expense - monthly_income
            # Prevent division by zero
            if burn == 0:
                runway_months = float("inf")
            else:
                runway_months = balance / burn

        future_balance = balance + (monthly_income * 6) - predicted_spend_6m

        # Return rounded values
        return {
            "monthly_income": round(monthly_income, 2),
            "monthly_expense": round(monthly_expense, 2),
            "runway_months": round(runway_months, 1) if runway_months != float("inf") else float("inf"),
            "future_balance": round(future_balance, 2)
        }

    except Exception as e:
        # ---------------- Error handling ----------------
        print(f"Error in cash_flow_forecast: {e}")
        # Return safe fallback values
        return {
            "monthly_income": 0,
            "monthly_expense": 0,
            "runway_months": 0,
            "future_balance": balance if isinstance(balance, (int, float)) else 0
        }
