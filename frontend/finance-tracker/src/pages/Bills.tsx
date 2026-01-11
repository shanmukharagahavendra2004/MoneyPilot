import { useEffect, useState } from "react";
import axios from "../axiosInstance";
import Sidebar from "./Sidebar"; // make sure this is correct path

interface Bill {
  id: number;
  billName: string;
  category: string;
  amount: number;
  paymentStatus: string;
  createdAt: string;
}

export default function Bills() {
  const [bills, setBills] = useState<Bill[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchBills();
  }, []);

 const fetchBills = async () => {
  try {
    // ✅ No need to get userId from localStorage
    const res = await axios.get(`/api/bills/user`); // backend uses JWT to know user
    setBills(res.data);
  } catch (err) {
    console.error("Fetch failed", err);
  } finally {
    setLoading(false);
  }
};


  return (
    // ❌ NO min-h-screen here
    // ❌ NO bg-gray-100
    <div className="flex h-full">
      {/* Sidebar */}
      <Sidebar />

      {/* Main content */}
      <div className="flex-1 bg-white text-black p-10">
        <h1 className="text-5xl font-black text-violet-500 text-center my-10">
          My Bills
        </h1>

        {loading ? (
          <p className="text-center text-gray-400">Loading bills...</p>
        ) : bills.length === 0 ? (
          <p className="text-center text-gray-400">No bills found</p>
        ) : (
          <table className="w-full border border-gray-700">
            <thead className="bg-violet-500 text-white text-xl">
              <tr>
                <th className="p-3 border">Bill</th>
                <th className="p-3 border">Category</th>
                <th className="p-3 border">Amount (₹)</th>
                <th className="p-3 border">Status</th>
                <th className="p-3 border">Date</th>
              </tr>
            </thead>
            <tbody>
              {bills.map((b) => (
                <tr key={b.id} className="text-center">
                  <td className="p-3 border">{b.billName}</td>
                  <td className="p-3 border">{b.category}</td>
                  <td className="p-3 border">₹{b.amount.toFixed(2)}</td>
                  <td className="p-3 border text-green-600">
                    {b.paymentStatus}
                  </td>
                  <td className="p-3 border">
                    {new Date(b.createdAt).toLocaleDateString()}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
