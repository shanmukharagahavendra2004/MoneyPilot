import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const Signup = () => {
  const [userName, setuserName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [salary, setSalary] = useState('');

  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();

    const user = { username:userName, email, password, salary: Number(salary) };

    try {
      const res = await axios.post('http://localhost:9090/api/users/signup', user);
      

      setuserName('');
      setEmail('');
      setPassword('');
      setSalary('');

      navigate('/login');

    } catch (error) {
      console.error(error);
      alert('Signup failed');
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center px-4 bg-gray-100">
      <form className="flex flex-col gap-4 w-full max-w-md bg-white p-6 sm:p-8 rounded-xl shadow-lg" onSubmit={handleSubmit}>
        
        <div className="flex justify-center">
          <h1 className="text-center font-bold text-3xl sm:text-4xl p-2 text-[#47f712] font-serif">
            Signup
          </h1>
        </div>

        <input
          className="border-2 border-black p-2 sm:p-3 w-full focus:outline-none focus:border-blue-300 rounded"
          type="text"
          placeholder="Enter the name"
          value={userName}
          onChange={(e) => setuserName(e.target.value)}
          required
        />

        <input
          className="border-2 border-black p-2 sm:p-3 w-full focus:outline-none focus:border-blue-700 rounded"
          type="email"
          placeholder="Enter the email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />

        <input
          className="border-2 border-black p-2 sm:p-3 w-full focus:outline-none focus:border-blue-700 rounded"
          type="password"
          placeholder="Enter the password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />

         <input
          className="border-2 border-black p-2 sm:p-3 w-full focus:outline-none focus:border-blue-700 rounded"
          type="number"
          placeholder="Enter your monthly salary"
          value={salary}
          onChange={(e) => setSalary(e.target.value)}
          required
        />



        <button
          className="border-2 border-black bg-amber-300 p-2 sm:p-3 w-full font-bold cursor-pointer text-lg sm:text-xl rounded hover:bg-amber-400 transition"
          type="submit"
        >
          Signup
        </button>
      </form>
    </div>
  );
};

export default Signup;
