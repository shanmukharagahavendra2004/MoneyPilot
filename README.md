# 💰 MoneyPilot – AI-Powered CFO Engine

MoneyPilot is a full-stack fintech application that helps users track income, manage expenses, make secure payments, and get AI-driven financial insights. It acts as a **personal CFO**, telling users what they spend, save, and whether they can afford a purchase — all in real time.

---

## 🚀 What Problem It Solves

Most people don’t know:

* Where their money goes
* How much they really save
* Whether they can afford something before buying

MoneyPilot solves this by combining **secure payments, expense tracking, and AI-based analysis** into a single platform.

---

## 🧠 Key Features

* 🔐 **JWT Authentication** – Secure login and user isolation
* 💳 **Razorpay Integration** – Real-world payment simulation
* 📊 **Expense & Category Tracking**
* 🤖 **AI Finance Assistant** – Ask questions like:

  * *“How much do I spend monthly?”*
  * *“Can I afford a ₹15,000 phone?”*
  * *“How much will I save in 6 months?”*
* 📈 **Monthly & Predictive Analytics**
* 🧾 **Salary vs Spending Insights**

---

## 🏗 System Architecture

MoneyPilot follows a **microservice-style architecture**:

```
React Frontend
       |
Spring Boot Backend (Auth, Payments, APIs)
       |
PostgreSQL Database
       |
Flask AI Engine (Analytics & Predictions)
```

Each service is independently responsible for a critical part of the system.

---

## 🛠 Tech Stack

### Frontend

* React.js
* TypeScript
* Tailwind CSS
* Axios
* Recharts

### Backend

* Spring Boot
* Spring Security (JWT)
* PostgreSQL
* Razorpay API

### AI & Analytics

* Python
* Flask
* Pandas
* SQLAlchemy

---

## 🔐 Authentication Flow

1. User logs in
2. Spring Boot generates a **JWT token**
3. Token is stored in the browser
4. All future API requests use this token
5. AI engine receives the `userId` extracted from JWT

This ensures all financial data is **secure and user-specific**.

---

## 💳 Payment Flow (Razorpay)

1. User enters an amount and category
2. Backend creates a Razorpay order
3. User completes payment
4. Razorpay verifies the payment
5. Payment is stored in PostgreSQL
6. AI engine immediately uses this data for insights

---

## 🤖 AI Engine Logic

The AI service analyzes:

* Salary
* This month’s expenses
* Past spending trends

It answers financial questions like:

> “Can I afford this?”
> “How much do I spend?”
> “How much will I save in 6 months?”

All calculations are based on **real user data**, not dummy values.

---

## 📊 How Affordability Works

MoneyPilot checks:

```
Remaining Balance = Salary − This Month’s Expenses
```

If:

```
Remaining ≥ Item Price → You can afford it
Else → It warns the user
```

This ensures **real-world accuracy**, just like a banking app.

---

## 🧪 Running the Project Locally

### Backend (Spring Boot)

```bash
mvn spring-boot:run
```

### AI Engine (Flask)

```bash
python app.py
```

### Frontend

```bash
npm install
npm run dev
```

---

## 🏦 Why This Is a Real Fintech-Grade Project

MoneyPilot is not just a CRUD app.
It includes:

* Authentication
* Secure payments
* Data analytics
* Financial logic
* AI decision-making

These are the same systems used in **banking, fintech, and product companies**.

---

## 👨‍💻 Author

Developed by **Shanmukha Raghavendra**
Full-Stack Developer | Fintech | AI | Spring Boot | React

---


