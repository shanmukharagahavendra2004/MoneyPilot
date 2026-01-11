def financial_health(salary, transactions):
    total_spent = transactions["amount"].sum()
    ratio = total_spent / salary

    if ratio < 0.5:
        return "Excellent"
    elif ratio < 0.75:
        return "Good"
    else:
        return "Overspending"
