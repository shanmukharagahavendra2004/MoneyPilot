import "./App.css";
import { BrowserRouter, Routes, Route } from "react-router-dom";

import Home from "./pages/Home";
import Landing from "./pages/Landing";
import Signup from "./pages/Signup";
import Login from "./pages/Login";

import Payments from "./pages/Payments";
import Subscriptions from "./pages/Subscriptions";
import Bills from "./pages/Bills";
import AddBill from "./pages/Addbill";
import MonthlyExpense from "./pages/MonthlyExpense";
import Logout from "./pages/Logout";

import DashboardLayout from "./pages/DashboardLayout";
import AiCFO from "./pages/AiCFO";

function App() {
  return (
    <BrowserRouter>
      {/* Navbar (visible everywhere) */}
      <div className="sticky top-0 z-50">
        <Home />
      </div>

      <Routes>
        {/* Public routes (NO sidebar) */}
        <Route path="/" element={<Landing />} />
        <Route path="/signup" element={<Signup />} />
        <Route path="/login" element={<Login />} />

        {/* Dashboard routes (WITH sidebar) */}
        <Route element={<DashboardLayout />}>
          <Route path="/payments" element={<Payments />} />
          <Route path="/subscriptions" element={<Subscriptions />} />
          <Route path="/bills" element={<Bills />} />
          <Route path="/addbill" element={<AddBill />} />
          <Route path="/monthlyexpense" element={<MonthlyExpense />} />
          <Route path="/logout" element={<Logout />} />
          <Route path="/aicfo" element={<AiCFO />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
