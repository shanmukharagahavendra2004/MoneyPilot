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
    # Defensive check: user_id must be positive integer
    if not isinstance(user_id, int) or user_id <= 0:
        print(f"Invalid user_id: {user_id}. Must be a positive integer.")
        return pd.DataFrame(columns=["amount", "category", "created_at"])

    query = text("""
        SELECT amount, category, created_at
        FROM transactions
        WHERE user_id = :user_id
        ORDER BY created_at
    """)

    try:
        df = pd.read_sql(query, engine, params={"user_id": user_id})

        # Defensive check: ensure expected columns exist
        expected_cols = {"amount", "category", "created_at"}
        if not expected_cols.issubset(df.columns):
            raise ValueError(f"Missing expected columns in DB result: {df.columns.tolist()}")

        # Optional: ensure proper data types
        df["amount"] = pd.to_numeric(df["amount"], errors="coerce").fillna(0)
        df["category"] = df["category"].astype(str)
        df["created_at"] = pd.to_datetime(df["created_at"], errors="coerce")

        return df

    except Exception as e:
        print(f"Error fetching transactions for user_id={user_id}: {e}")
        return pd.DataFrame(columns=["amount", "category", "created_at"])


def get_salary(user_id: int) -> float:
    """
    Fetch salary of a user by user_id.
    Returns 0.0 if user not found or error occurs.
    """
    # Defensive check: user_id must be positive integer
    if not isinstance(user_id, int) or user_id <= 0:
        print(f"Invalid user_id: {user_id}. Must be a positive integer.")
        return 0.0

    query = text("SELECT salary FROM users WHERE id = :user_id")

    try:
        df = pd.read_sql(query, engine, params={"user_id": user_id})

        if df.empty:
            print(f"No salary record found for user_id={user_id}. Returning 0.0")
            return 0.0

        # Defensive: ensure salary is a valid number
        salary_value = pd.to_numeric(df.iloc[0]["salary"], errors="coerce")
        if pd.isna(salary_value) or salary_value < 0:
            print(f"Invalid salary value for user_id={user_id}: {df.iloc[0]['salary']}. Returning 0.0")
            return 0.0

        return float(salary_value)

    except Exception as e:
        print(f"Error fetching salary for user_id={user_id}: {e}")
        return 0.0
