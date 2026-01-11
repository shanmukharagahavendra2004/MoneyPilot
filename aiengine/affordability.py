def can_afford(salary, predicted_expenses, cost):
    free_money = salary * 6 - predicted_expenses
    return free_money >= cost
