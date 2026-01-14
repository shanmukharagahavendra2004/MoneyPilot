def financial_health(salary, transactions):
    try:
        # Defensive checks
        if not isinstance(salary, (int, float)) or salary <= 0:
            raise ValueError("Salary must be a positive number.")
        
        if not hasattr(transactions, "columns") or "amount" not in transactions.columns:
            raise ValueError("Transactions must be a DataFrame with an 'amount' column.")

        # Ensure amounts are numeric
        total_spent = pd.to_numeric(transactions["amount"], errors="coerce").fillna(0).sum()

        ratio = total_spent / salary

        if ratio < 0.5:
            return "Excellent"
        elif ratio < 0.75:
            return "Good"
        else:
            return "Overspending"

    except Exception as e:
        print(f"Error in financial_health: {e}")
        # Safe fallback
        return "Unknown"
