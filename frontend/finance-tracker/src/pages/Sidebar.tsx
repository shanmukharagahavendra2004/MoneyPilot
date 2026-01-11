import React from "react";
import { Link } from "react-router-dom";

interface SidebarProps {
  isOpen?: boolean;
}

const Sidebar: React.FC<SidebarProps> = ({ isOpen = true }) => {
  return (
    <aside
      className={`
        fixed left-0 top-16
        h-[calc(100vh-4rem)] w-64
        bg-[#47f712] text-white z-40
        transition-transform duration-300 ease-in-out
        ${isOpen ? "translate-x-0" : "-translate-x-full"}
        md:translate-x-0
      `}
    >
      <nav className="flex flex-col gap-2 p-4 font-bold text-lg lg:text-xl font-serif overflow-y-auto">
        <Link to="/monthlyexpense" className="p-3 rounded hover:bg-white/20">
          Analytics Dashboard
        </Link>

        <Link to="/aicfo" className="p-3 rounded hover:bg-white/20">
          AI CFO
        </Link>

        <Link to="/payments" className="p-3 rounded hover:bg-white/20">
          Payments
        </Link>

        <Link to="/bills" className="p-3 rounded hover:bg-white/20">
          Bills
        </Link>

        <Link
          to="/logout"
          className="p-3 rounded text-red-900 hover:bg-white font-bold"
        >
          Logout
        </Link>
      </nav>
    </aside>
  );
};

export default Sidebar;
