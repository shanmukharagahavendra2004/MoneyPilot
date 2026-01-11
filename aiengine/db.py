from sqlalchemy import create_engine, text
import pandas as pd

# ---------------- Database Setup ----------------
# Replace 'password' with your actual DB password
DB_URL = "postgresql://postgres:password@localhost:5432/financetracker"
engine = create_engine(DB_URL)


def get_transactions(user_id: int) -> pd.DataFrame:
    """
    Fetch all transactions for a user ordered by creation date.
    Returns a pandas DataFrame.
    """
    query = text("""
        SELECT amount, category, created_at
        FROM transactions
        WHERE user_id = :user_id
        ORDER BY created_at
    """)

    try:
        df = pd.read_sql(query, engine, params={"user_id": user_id})
        return df
    except Exception as e:
        print(f"Error fetching transactions for user_id={user_id}: {e}")
        return pd.DataFrame(columns=["amount", "category", "created_at"])


def get_salary(user_id: int) -> float:
    """
    Fetch salary of a user by user_id.
    Returns 0.0 if user not found.
    """
    query = text("SELECT salary FROM users WHERE id = :user_id")

    try:
        df = pd.read_sql(query, engine, params={"user_id": user_id})
        if df.empty:
            return 0.0
        return float(df.iloc[0]["salary"])
    except Exception as e:
        print(f"Error fetching salary for user_id={user_id}: {e}")
        return 0.0
