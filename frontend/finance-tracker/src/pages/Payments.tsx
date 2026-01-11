import React, { useState } from "react";
import axios from "../axiosInstance";

const Payments = () => {
  const [billName, setBillName] = useState("");
  const [amount, setAmount] = useState("");
  const [category, setCategory] = useState("");

  const [successMsg, setSuccessMsg] = useState("");
  const [errorMsg, setErrorMsg] = useState("");

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    setSuccessMsg("");
    setErrorMsg("");

    if (!billName || !amount || !category) {
      setErrorMsg("Please fill all fields");
      return;
    }

    try {
      const amountInRupees = Number(amount);
      const amountInPaise = amountInRupees;

      const orderResponse = await axios.post("/api/payments/create-order", null, { params: { amount: amountInRupees } });

      const order = orderResponse.data;

      const options = {
        key: "rzp_test_RtA82PeJBLI8bY",
        amount: order.amount,
        currency: "INR",
        name: "Finance Tracker",
        description: "Bill Payment",
        order_id: order.id,

        handler: async (response: any) => {
          console.log("Razorpay Response:", response);

          try {
            // ✅ Remove userId, backend will extract user from JWT
            await axios.post("/api/payments/verify", {
              razorpay_payment_id: response.razorpay_payment_id,
              razorpay_order_id: response.razorpay_order_id,
              razorpay_signature: response.razorpay_signature,
              amount: amountInPaise,
              paymentType: category,
              billName,
              category,
            });

            setSuccessMsg("Payment Successful 🎉");
            setBillName("");
            setAmount("");
            setCategory("");
          } catch (err) {
            console.error("Verification failed", err);
            setErrorMsg("Payment verification failed");
          }
        },

        theme: { color: "#facc15" },
      };

      const rzp = new (window as any).Razorpay(options);
      rzp.open();
    } catch (err) {
      console.error("Order creation failed", err);
      setErrorMsg("Unable to create payment order");
    }
  };

  return (
    <div className="min-h-screen bg-white flex items-center justify-center px-4">
      <div className="w-full max-w-xl text-center">
        <h1 className="text-3xl sm:text-5xl text-amber-400 font-extrabold mb-8 sm:mb-10">
          Pay Your Bills
        </h1>

        <div className="bg-yellow-100 border-l-4 border-yellow-500 text-yellow-900 p-4 mb-6 rounded text-left">
          <p className="font-bold">⚠ Razorpay Test Mode</p>
          <p className="text-sm sm:text-base">
            This is a demo payment system using Razorpay Test API.  
            No real money will be deducted.
          </p>
          <p className="text-sm mt-1">
            For UPI testing, use{" "}
            <span className="font-mono text-black font-bold">
              success@razorpay
            </span>
          </p>
        </div>

        <form onSubmit={handleSubmit} className="flex flex-col gap-4 sm:gap-6">
          <input
            className="p-3 sm:p-4 bg-white border border-gray-500 text-black rounded"
            placeholder="Enter Bill Name"
            value={billName}
            onChange={(e) => setBillName(e.target.value)}
          />

          <input
            className="p-3 sm:p-4 border border-gray-500 text-black rounded"
            type="number"
            placeholder="Enter Amount (₹)"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
          />

          <select
            className="p-3 sm:p-4 bg-white border border-gray-500 text-black rounded"
            value={category}
            onChange={(e) => setCategory(e.target.value)}
          >
            <option value="">Choose category</option>
            <option value="Rent">Rent</option>
            <option value="Utilities">Utilities</option>
            <option value="Maintenance">Maintenance</option>
            <option value="OTT">OTT / Streaming</option>
            <option value="Internet">Internet</option>
            <option value="Mobile">Mobile Recharge</option>
            <option value="Groceries">Groceries</option>
            <option value="Food">Food & Dining</option>
            <option value="Shopping">Shopping</option>
            <option value="Transport">Transport</option>
            <option value="Fuel">Fuel</option>
            <option value="EMI">EMI / Loan</option>
            <option value="Insurance">Insurance</option>
            <option value="CreditCard">Credit Card</option>
            <option value="Medical">Medical</option>
            <option value="Fitness">Fitness</option>
            <option value="Education">Education</option>
            <option value="Entertainment">Entertainment</option>
            <option value="Other">Other</option>
          </select>

          <button
            type="submit"
            className="bg-yellow-400 text-black py-3 sm:py-4 font-bold text-lg rounded cursor-pointer hover:bg-yellow-300 transition"
          >
            Pay Now
          </button>

          {successMsg && (
            <p className="text-green-600 font-bold text-base sm:text-lg">
              {successMsg}
            </p>
          )}

          {errorMsg && (
            <p className="text-red-600 font-bold text-base sm:text-lg">
              {errorMsg}
            </p>
          )}
        </form>
      </div>
    </div>
  );
};

export default Payments;
