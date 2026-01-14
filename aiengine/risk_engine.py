def calculate_risk(monthly_income, monthly_expense, runway):
    try:
        # ---------------- Defensive checks ----------------
        for name, value in [("monthly_income", monthly_income),
                            ("monthly_expense", monthly_expense),
                            ("runway", runway)]:
            if not isinstance(value, (int, float)):
                raise ValueError(f"{name} must be a number. Got {type(value)} instead.")
        
        if monthly_income < 0 or monthly_expense < 0 or runway < 0:
            raise ValueError("Monthly income, monthly expense, and runway cannot be negative.")

        # Prevent division by zero
        if monthly_income == 0:
            return "CRITICAL" if monthly_expense > 0 else "LOW"

        savings_rate = (monthly_income - monthly_expense) / monthly_income

        if monthly_expense > monthly_income and runway < 3:
            return "CRITICAL"
        elif monthly_expense > monthly_income:
            return "HIGH"
        elif savings_rate < 0.2:
            return "MEDIUM"
        else:
            return "LOW"

    except Exception as e:
        print(f"Error in calculate_risk: {e}")
        # Safe fallback
        return "UNKNOWN"
