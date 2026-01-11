import React from "react";
import { Link } from "react-router-dom";

const Home = () => {
  return (
    <>
      {/* Fixed Navbar */}
      <div className="fixed top-0 left-0 w-full bg-[#47f712] flex items-center z-50 shadow-md">
        <h1 className="font-bold text-white text-5xl p-4">
          Personal Finance Tracker
        </h1>

        <div className="flex gap-4 text-xl  text-white ml-auto mr-6 font-bold font-mono">
          <Link className="border-2 p-2 rounded-2xl bg-blue-950" to="/signup">
            Signup
          </Link>
          <Link className="border-2 p-2 rounded-2xl bg-blue-950" to="/login">
            Login
          </Link>
        </div>
      </div>

      {/* Spacer to prevent content hiding */}
      <div className="h-20"></div>
    </>
  );
};

export default Home;
