import React, { useEffect, useState } from "react";
import axios from "../axiosInstance";
import {
  PieChart,
  Pie,
  Tooltip,
  Cell,
  ResponsiveContainer,
  Legend,
} from "recharts";

const COLORS = [
  "#fbbf24", "#60a5fa", "#34d399", "#f87171", "#a78bfa",
  "#f472b6", "#22d3ee", "#facc15", "#818cf8", "#ec4899"
];

interface PieData {
  category: string;
  totalAmount: number;
}

const MonthlyExpense = () => {
  const [data, setData] = useState<PieData[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const now = new Date();
      const month = now.getMonth() + 1;
      const year = now.getFullYear();

      const res = await axios.get("/api/analytics/monthly-category", {
        params: { month, year },
      });

      const formatted: PieData[] = res.data.map((item: any) => ({
        category: item.category,
        totalAmount: item.totalAmount,
      }));

      setData(formatted);
    } catch (error) {
      console.error("Failed to fetch monthly analytics", error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-gray-900 p-4 sm:p-6 rounded-lg shadow-lg w-full">
      <h2 className="text-lg sm:text-2xl text-amber-300 font-bold mb-4 text-center">
        Monthly Expenses by Category
      </h2>

      {loading ? (
        <p className="text-gray-400 text-center">Loading...</p>
      ) : data.length === 0 ? (
        <p className="text-gray-400 text-center">
          No expenses recorded this month
        </p>
      ) : (
        <div className="w-full h-[280px] sm:h-[350px]">
       

          <ResponsiveContainer width="100%" height="100%">
            <PieChart>
              <Pie
                data={data}
                dataKey="totalAmount"
                nameKey="category"
                cx="50%"
                cy="50%"
                outerRadius="70%"
                // 1. Added labelLine to pull labels away from the center
                labelLine={true} 
                // 2. Added minAngle so tiny slices don't disappear and overlap labels
                minAngle={5} 
                // 3. Added paddingAngle to separate the slices visually
                paddingAngle={2}
                label={({ name, value }) => `${name}: ₹${value}`}
              >
                {data.map((_, index) => (
                  <Cell
                    key={`cell-${index}`}
                    fill={COLORS[index % COLORS.length]}
                  />
                ))}
              </Pie>

              <Tooltip formatter={(value) => `₹${value}`} />
              <Legend layout="horizontal" verticalAlign="bottom" align="center" />
            </PieChart>
          </ResponsiveContainer>


        </div>
      )}
    </div>
  );
};

export default MonthlyExpense;
