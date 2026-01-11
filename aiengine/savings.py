def saving_recommendations(transactions, target):
    by_category = transactions.groupby("category")["amount"].sum()
    sorted_cats = by_category.sort_values(ascending=False)

    result = []
    saved = 0

    for cat, amt in sorted_cats.items():
        cut = round(amt * 0.3, 2)
        result.append({"category": cat, "reduce_by": cut})
        saved += cut
        if saved >= target:
            break

    return result
