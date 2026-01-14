import openai

def llm_cfo_advice(data):
    try:
        # ---------------- Defensive checks ----------------
        required_keys = ["income", "expense", "runway", "risk", "categories"]
        missing_keys = [k for k in required_keys if k not in data]
        if missing_keys:
            raise ValueError(f"Missing keys in data: {missing_keys}")

        # Ensure numeric values are valid
        for key in ["income", "expense", "runway", "risk"]:
            if not isinstance(data[key], (int, float)):
                raise ValueError(f"{key} must be a number. Got {type(data[key])} instead.")

        # Ensure categories is a list
        if not isinstance(data["categories"], (list, tuple)):
            raise ValueError(f"'categories' must be a list or tuple. Got {type(data['categories'])} instead.")

        # ---------------- Build prompt ----------------
        prompt = f"""
You are a CFO for a fintech app.
Here is the user's financial data:

Income: {data['income']}
Monthly Expense: {data['expense']}
Runway: {data['runway']} months
Risk: {data['risk']}
Top Categories: {data['categories']}

Give 3 very practical financial actions.
"""

        # ---------------- Call OpenAI ----------------
        response = openai.ChatCompletion.create(
            model="gpt-4",
            messages=[{"role": "user", "content": prompt}]
        )

        return response.choices[0].message.content

    except Exception as e:
        print(f"Error in llm_cfo_advice: {e}")
        # Safe fallback advice
        return (
            "Unable to provide advice at the moment. "
            "Please check your financial data or try again later."
        )
