def can_afford(salary, predicted_expenses, cost):
    try:
        # Defensive checks: ensure inputs are numbers
        if not all(isinstance(x, (int, float)) for x in [salary, predicted_expenses, cost]):
            raise ValueError("All inputs must be numbers (int or float).")
        
        # Defensive checks: negative values
        if salary < 0 or predicted_expenses < 0 or cost < 0:
            raise ValueError("Salary, predicted expenses, and cost cannot be negative.")
        
        free_money = salary * 6 - predicted_expenses
        return free_money >= cost

    except Exception as e:
        # Log the error (here we just print, replace with logging in real apps)
        print(f"Error in can_afford: {e}")
        # Return False as safe fallback
        return False
