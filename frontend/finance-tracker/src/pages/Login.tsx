import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import {jwtDecode} from "jwt-decode";

interface DecodedToken {
  sub: string;      // username
  userId: number;   // custom claim from backend
  iat: number;
  exp: number;
}

const Login = () => {
  const [userName, setUserName] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      const res = await axios.post("http://localhost:9090/api/users/login", {
        username: userName,
        password,
      });

      console.log("Login response:", res.data);
      const token = res.data;
      console.log("Received token:", token);

      if (!token) throw new Error("No token received from backend");
      console.log("Received token:", token);
      // ✅ Save to localStorage
      localStorage.setItem("token", token);

      // 🔥 Optional: decode JWT
      const decoded: DecodedToken = jwtDecode(token);
      console.log("Decoded JWT:", decoded);

      // ✅ Confirm token is saved
      console.log("Saved token:", localStorage.getItem("token"));

      // ✅ Navigate only after token is saved
      navigate("/bills");
    } catch (error: any) {
      console.error("Login failed:", error.response?.data || error.message);
      alert("Invalid username or password");
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center px-4 bg-gray-100">
      <form
        className="flex flex-col gap-4 w-full max-w-md bg-white p-6 sm:p-8 rounded-xl shadow-lg"
        onSubmit={handleSubmit}
      >
        <h1 className="text-center font-bold text-3xl sm:text-4xl p-2 text-[#47f712] font-serif">
          Login
        </h1>

        <input
          type="text"
          placeholder="Enter the UserName"
          value={userName}
          onChange={(e) => setUserName(e.target.value)}
          required
          className="border-2 border-black p-2 sm:p-3 w-full focus:outline-none focus:border-blue-700 rounded"
        />

        <input
          type="password"
          placeholder="Enter the password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
          className="border-2 border-black p-2 sm:p-3 w-full focus:outline-none focus:border-blue-700 rounded"
        />

        <button
          type="submit"
          className="border-2 bg-amber-300 border-black p-2 sm:p-3 w-full cursor-pointer font-bold text-lg sm:text-xl rounded hover:bg-amber-400 transition"
        >
          Login
        </button>
      </form>
    </div>
  );
};

export default Login;
