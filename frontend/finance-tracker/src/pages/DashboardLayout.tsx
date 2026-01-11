import Sidebar from "./Sidebar";
import { Outlet } from "react-router-dom";

const DashboardLayout = () => {
  return (
    <div className="flex min-h-screen bg-gray-100 overflow-hidden">
      {/* Sidebar */}
      <Sidebar />

      {/* Main content */}
      <div className="flex-1 ml-0 md:ml-64 bg-black overflow-y-auto">
        <Outlet />
      </div>
    </div>
  );
};

export default DashboardLayout;
