import React, { useState } from "react";
import axios from "axios";

const AddBill = () => {
  const [billName, setBillName] = useState("");
  const [amount, setAmount] = useState("");
  const [category, setCategory] = useState("");
  const [loading, setLoading] = useState(false);

  const handleAddBill = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!billName || !amount || !category) {
      alert("Please fill all fields");
      return;
    }

    try {
      setLoading(true);

      const response = await axios.post(
        "http://localhost:9090/api/bills/add",
        {
          billName,
          amount: Number(amount),
          category,
          userId: "USER123",
          paymentStatus: "PAID" // replace with logged-in user
        },
        {
          headers: {
            "Content-Type": "application/json"
          }
        }
      );

      console.log("Bill added:", response.data);
      alert("Bill added successfully ✅");

      // reset form
      setBillName("");
      setAmount("");
      setCategory("");

    } catch (error) {
      console.error("Error adding bill", error);
      alert("Failed to add bill ❌");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-black min-h-screen flex flex-col items-center">
      <h1 className="text-5xl font-bold text-violet-600 mt-10">
        Add Bill
      </h1>

      <form
        onSubmit={handleAddBill}
        className="flex flex-col gap-6 mt-16 w-80"
      >
        <input
          className="p-2 bg-black border border-slate-500 text-white"
          type="text"
          placeholder="Bill Name"
          value={billName}
          onChange={(e) => setBillName(e.target.value)}
        />

        <input
          className="p-2 bg-black border border-slate-500 text-white"
          type="number"
          placeholder="Amount"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
        />

        <select
          className="p-2 bg-black border border-slate-500 text-white"
          value={category}
          onChange={(e) => setCategory(e.target.value)}
        >
          <option value="">Select Category</option>
          <option value="Utilities">Utilities</option>
          <option value="Rent">Rent</option>
          <option value="OTT">OTT</option>
          <option value="Recharge">Recharge</option>
          <option value="Insurance">Insurance</option>
          <option value="Other">Other</option>
        </select>

        <button
          type="submit"
          disabled={loading}
          className="bg-violet-700 text-white py-2 hover:bg-violet-900"
        >
          {loading ? "Saving..." : "Add Bill"}
        </button>
      </form>
    </div>
  );
};

export default AddBill;
