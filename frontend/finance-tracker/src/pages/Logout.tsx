import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

const Logout = () => {
  const navigate = useNavigate();

  useEffect(() => {
    // 🔐 Clear authentication data
    localStorage.removeItem("token");
    localStorage.removeItem("userId");

    // 🚀 Redirect to Home page
    navigate("/", { replace: true });
  }, [navigate]);

  return null; // nothing to render
};

export default Logout;
