from prophet import Prophet

def predict_next_6_months(transactions):
    df = transactions.groupby("created_at")["amount"].sum().reset_index()
    df.columns = ["ds", "y"]

    model = Prophet()
    model.fit(df)

    future = model.make_future_dataframe(periods=180)
    forecast = model.predict(future)

    predicted = forecast.tail(180)["yhat"].sum()
    return predicted
