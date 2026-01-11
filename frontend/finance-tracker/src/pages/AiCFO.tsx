import { useState } from "react";
import axios from "../axiosInstance";

interface Message {
  role: "user" | "ai";
  content: string;
}

const AiCFO = () => {
  const [question, setQuestion] = useState("");
  const [messages, setMessages] = useState<Message[]>([]);
  const [loading, setLoading] = useState(false);

  const askAI = async () => {
    if (!question.trim()) return;

    const userMessage: Message = {
      role: "user",
      content: question,
    };

    setMessages((prev) => [...prev, userMessage]);
    setQuestion("");
    setLoading(true);

    try {
    const res = await axios.post("http://localhost:9090/api/ai/query", {
  question
});




      const aiMessage: Message = {
        role: "ai",
        content: res.data.answer,
      };

      setMessages((prev) => [...prev, aiMessage]);
    } catch {
      setMessages((prev) => [
        ...prev,
        { role: "ai", content: "❌ AI Engine unavailable" },
      ]);
    }

    setLoading(false);
  };

  return (
    <div className="h-screen bg-gradient-to-br from-black via-[#0f172a] to-black flex flex-col overflow-hidden">
      
      {/* Header */}
      <div className="border-b border-gray-800 p-4 sm:p-6">
        <h1 className="text-xl sm:text-3xl font-extrabold text-white">
          AI Personal CFO
        </h1>
        <p className="text-gray-400 mt-1 text-sm sm:text-base">
          Your intelligent financial decision engine
        </p>
      </div>

      {/* Chat */}
      <div className="flex-1 overflow-y-auto px-3 sm:px-6 py-6 space-y-4">
        {messages.length === 0 && (
          <div className="text-center mt-20 px-4">
            <p className="text-gray-400 text-base sm:text-lg">
              Ask things like:
            </p>
            <div className="mt-6 space-y-3">
              <p className="text-indigo-400 text-sm sm:text-base">
                “Can I afford a ₹15,000 phone this month?”
              </p>
              <p className="text-indigo-400 text-sm sm:text-base">
                “How much will I save in 6 months?”
              </p>
              <p className="text-indigo-400 text-sm sm:text-base">
                “What should I cut to save ₹5,000?”
              </p>
            </div>
          </div>
        )}

        {messages.map((msg, index) => (
          <div
            key={index}
            className={`max-w-[90%] sm:max-w-xl p-4 sm:p-5 rounded-xl break-words ${
              msg.role === "user"
                ? "ml-auto bg-indigo-600 text-white"
                : "mr-auto bg-gray-800 text-gray-200"
            }`}
          >
            {msg.content}
          </div>
        ))}

        {loading && (
          <div className="mr-auto bg-gray-800 text-gray-300 px-5 py-3 rounded-xl animate-pulse max-w-[80%]">
            AI is analyzing your finances...
          </div>
        )}
      </div>

      {/* Input */}
      <div className="border-t border-gray-800 p-3 sm:p-6 bg-black">
        <div className="flex gap-3 sm:gap-4">
          <input
            value={question}
            onChange={(e) => setQuestion(e.target.value)}
            placeholder="Ask your AI CFO..."
            className="flex-1 bg-gray-900 border border-gray-700 text-white px-4 sm:px-5 py-3 sm:py-4 rounded-xl focus:outline-none focus:border-indigo-500 text-sm sm:text-base"
            onKeyDown={(e) => e.key === "Enter" && askAI()}
          />

          <button
            onClick={askAI}
            className="bg-indigo-600 hover:bg-indigo-500 text-white px-5 sm:px-8 rounded-xl font-bold transition text-sm sm:text-base"
          >
            Ask
          </button>
        </div>
      </div>
    </div>
  );
};

export default AiCFO;
