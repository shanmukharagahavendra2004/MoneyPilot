import axios from "axios";

const axiosInstance = axios.create({
  baseURL: "http://localhost:9090",
});

// ✅ Attach JWT automatically
axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token) {
      // Remove ALL whitespace
      const cleanToken = token.replace(/\s+/g, '');
      config.headers.Authorization = `Bearer ${cleanToken}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export default axiosInstance;
