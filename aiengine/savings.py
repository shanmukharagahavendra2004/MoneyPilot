def saving_recommendations(transactions, target):
    try:
        # ---------------- Defensive checks ----------------
        if not hasattr(transactions, "columns") or "category" not in transactions.columns or "amount" not in transactions.columns:
            raise ValueError("Transactions must be a DataFrame with 'category' and 'amount' columns.")

        if transactions.empty:
            return []

        if not isinstance(target, (int, float)) or target <= 0:
            raise ValueError("Target must be a positive number.")

        # Ensure amounts are numeric
        transactions["amount"] = pd.to_numeric(transactions["amount"], errors="coerce").fillna(0)

        # ---------------- Group and sort ----------------
        by_category = transactions.groupby("category")["amount"].sum()
        sorted_cats = by_category.sort_values(ascending=False)

        result = []
        saved = 0

        for cat, amt in sorted_cats.items():
            cut = round(amt * 0.3, 2)  # Suggest reducing 30% per category
            result.append({"category": cat, "reduce_by": cut})
            saved += cut
            if saved >= target:
                break

        return result

    except Exception as e:
        print(f"Error in saving_recommendations: {e}")
        # Safe fallback
        return []
