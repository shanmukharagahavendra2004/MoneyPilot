import React, { useEffect, useMemo, useState } from "react";

type Frequency = "Monthly" | "Yearly" | "Weekly" | "One-time";

type Subscription = {
  id: string;
  name: string;
  provider?: string;
  amount: number;
  currency?: string;
  frequency: Frequency;
  nextRenewal: string; // ISO date
  autoRenew: boolean;
  category?: string;
  notes?: string;
  active: boolean;
};

const sampleData: Subscription[] = [
  {
    id: "sub_001",
    name: "Netflix (Standard)",
    provider: "Netflix",
    amount: 499,
    currency: "INR",
    frequency: "Monthly",
    nextRenewal: "2025-01-28",
    autoRenew: true,
    category: "OTT",
    active: true,
  },
  {
    id: "sub_002",
    name: "Amazon Prime",
    provider: "Amazon",
    amount: 1499,
    currency: "INR",
    frequency: "Yearly",
    nextRenewal: "2025-12-14",
    autoRenew: true,
    category: "OTT",
    active: true,
  },
  {
    id: "sub_003",
    name: "JioFiber",
    provider: "Jio",
    amount: 799,
    currency: "INR",
    frequency: "Monthly",
    nextRenewal: "2025-01-10",
    autoRenew: true,
    category: "Broadband",
    active: true,
  },
];

function formatDate(iso: string) {
  const d = new Date(iso);
  return d.toLocaleDateString();
}

export default function Subscriptions(): JSX.Element {
  const [subs, setSubs] = useState<Subscription[]>([]);
  const [query, setQuery] = useState("");
  const [categoryFilter, setCategoryFilter] = useState<string>("All");
  const [showAddModal, setShowAddModal] = useState(false);
  const [newSub, setNewSub] = useState<Partial<Subscription> | null>(null);
  const [selected, setSelected] = useState<Subscription | null>(null);

  useEffect(() => {
    // TODO: replace with API fetch
    setSubs(sampleData);
  }, []);

  const categories = useMemo(() => {
    const cats = new Set<string>();
    subs.forEach((s) => s.category ?? cats.add(s.category ?? "Other"));
    return ["All", ...Array.from(cats).filter(Boolean)];
  }, [subs]);

  const filtered = subs.filter((s) => {
    if (categoryFilter !== "All" && s.category !== categoryFilter) return false;
    if (!query) return true;
    return (
      s.name.toLowerCase().includes(query.toLowerCase()) ||
      (s.provider && s.provider.toLowerCase().includes(query.toLowerCase()))
    );
  });

  const upcomingCount = subs.filter((s) => {
    const diff =
      (new Date(s.nextRenewal).getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24);
    return diff <= 7 && diff >= 0;
  }).length;

  function openAddModal() {
    setNewSub({
      name: "",
      amount: 0,
      currency: "INR",
      frequency: "Monthly",
      nextRenewal: new Date().toISOString().slice(0, 10),
      autoRenew: true,
      category: "OTT",
      active: true,
    });
    setShowAddModal(true);
  }

  function saveNewSubscription() {
    if (!newSub || !newSub.name) return;
    const created: Subscription = {
      id: `sub_${Date.now()}`,
      name: newSub.name!,
      provider: newSub.provider,
      amount: Number(newSub.amount ?? 0),
      currency: newSub.currency ?? "INR",
      frequency: (newSub.frequency as Frequency) || "Monthly",
      nextRenewal: newSub.nextRenewal ?? new Date().toISOString().slice(0, 10),
      autoRenew: !!newSub.autoRenew,
      category: newSub.category ?? "Other",
      notes: newSub.notes,
      active: !!newSub.active,
    };
    // TODO: call backend POST /subscriptions
    setSubs((prev) => [created, ...prev]);
    setShowAddModal(false);
    setNewSub(null);
  }

  function handlePayNow(s: Subscription) {
    // TODO: implement payment creation flow -> backend -> Razorpay -> verify
    alert(`Pay Now: ${s.name} — Amount: ${s.currency} ${s.amount}`);
    // After success, update nextRenewal: increment based on frequency (basic example)
    const next = new Date(s.nextRenewal);
    if (s.frequency === "Monthly") next.setMonth(next.getMonth() + 1);
    else if (s.frequency === "Yearly") next.setFullYear(next.getFullYear() + 1);
    else if (s.frequency === "Weekly") next.setDate(next.getDate() + 7);
    // TODO: persist update to backend
    setSubs((prev) => prev.map((p) => (p.id === s.id ? { ...p, nextRenewal: next.toISOString().slice(0, 10) } : p)));
  }

  function handleManage(s: Subscription) {
    setSelected(s);
    // show manage modal or navigate to details page — stub
    alert(`Open manage for ${s.name}`);
  }

  function handleToggleAutoRenew(s: Subscription) {
    // TODO: call backend to persist
    setSubs((prev) => prev.map((p) => (p.id === s.id ? { ...p, autoRenew: !p.autoRenew } : p)));
  }

  return (
    <div className="p-6 max-w-5xl mx-auto">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-semibold text-gray-800">Subscriptions</h1>
          <p className="text-sm text-gray-500">
            Manage your recurring payments, renewals and spending.
          </p>
        </div>

        <div className="flex items-center gap-3">
          <div className="text-sm text-gray-700">
            <span className="font-medium">{subs.length}</span> active
          </div>
          <div className="text-sm text-yellow-600">
            <span className="font-medium">{upcomingCount}</span> upcoming
          </div>
          <button
            onClick={openAddModal}
            className="inline-flex items-center gap-2 bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-md text-sm"
          >
            + Add Subscription
          </button>
        </div>
      </div>

      {/* Controls */}
      <div className="flex flex-col md:flex-row gap-3 items-start md:items-center mb-6">
        <input
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Search by name or provider..."
          className="w-full md:w-1/2 px-3 py-2 border rounded-md focus:outline-none focus:ring-indigo-300"
        />
        <select
          value={categoryFilter}
          onChange={(e) => setCategoryFilter(e.target.value)}
          className="px-3 py-2 border rounded-md"
        >
          <option>All</option>
          <option>OTT</option>
          <option>Broadband</option>
          <option>Utilities</option>
          <option>Recharge</option>
          <option>Insurance</option>
          <option>Other</option>
        </select>
        <div className="ml-auto flex gap-2">
          <button
            onClick={() => {
              // simple sort: upcoming first
              setSubs((prev) =>
                [...prev].sort((a, b) => new Date(a.nextRenewal).getTime() - new Date(b.nextRenewal).getTime())
              );
            }}
            className="px-3 py-2 border rounded-md bg-white hover:bg-gray-50"
          >
            Sort by next renewal
          </button>
          <button
            onClick={() => {
              // show all (reset)
              setQuery("");
              setCategoryFilter("All");
              setSubs((prev) => [...prev]); // no-op to keep stable
            }}
            className="px-3 py-2 border rounded-md bg-white hover:bg-gray-50"
          >
            Reset
          </button>
        </div>
      </div>

      {/* List */}
      <div className="space-y-4">
        {filtered.map((s) => (
          <div
            key={s.id}
            className="flex items-center justify-between bg-white p-4 rounded-md shadow-sm border"
          >
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 bg-indigo-50 text-indigo-600 rounded-lg flex items-center justify-center font-semibold">
                {s.provider ? s.provider[0] : s.name[0]}
              </div>
              <div>
                <div className="flex items-center gap-2">
                  <h3 className="text-lg font-medium text-gray-800">{s.name}</h3>
                  {!s.active && <span className="text-xs text-red-600 px-2 py-1 bg-red-50 rounded">Inactive</span>}
                  {s.autoRenew && <span className="text-xs text-green-600 px-2 py-1 bg-green-50 rounded">Auto</span>}
                </div>
                <div className="text-sm text-gray-500">
                  {s.category} • {s.frequency} • Next: <span className="font-medium">{formatDate(s.nextRenewal)}</span>
                </div>
              </div>
            </div>

            <div className="flex items-center gap-4">
              <div className="text-right">
                <div className="text-green-600 font-semibold">
                  {s.currency} {s.amount}
                </div>
                <div className="text-sm text-gray-500">Billed {s.frequency.toLowerCase()}</div>
              </div>

              <div className="flex items-center gap-2">
                <button
                  onClick={() => handlePayNow(s)}
                  className="px-3 py-2 bg-indigo-600 text-white rounded-md text-sm hover:bg-indigo-700"
                >
                  Pay Now
                </button>

                <button
                  onClick={() => handleManage(s)}
                  className="px-3 py-2 border rounded-md text-sm hover:bg-gray-50"
                >
                  Manage
                </button>

                <button
                  onClick={() => handleToggleAutoRenew(s)}
                  className={`px-2 py-2 rounded-md text-sm border ${s.autoRenew ? "bg-green-50 border-green-200" : "bg-gray-50"}`}
                  title="Toggle Auto-Renew"
                >
                  {s.autoRenew ? "Auto ✓" : "Off"}
                </button>
              </div>
            </div>
          </div>
        ))}

        {filtered.length === 0 && (
          <div className="text-center text-gray-500 py-8 bg-white rounded-md border">No subscriptions found.</div>
        )}
      </div>

      {/* Add Modal */}
      {showAddModal && newSub && (
        <div className="fixed inset-0 z-40 flex items-center justify-center bg-black/40">
          <div className="bg-white rounded-lg w-full max-w-lg p-6">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-lg font-semibold">Add Subscription</h2>
              <button onClick={() => setShowAddModal(false)} className="text-gray-400 hover:text-gray-600">✕</button>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
              <div>
                <label className="block text-sm text-gray-600">Name</label>
                <input
                  className="mt-1 px-3 py-2 border rounded-md w-full"
                  value={newSub.name ?? ""}
                  onChange={(e) => setNewSub({ ...newSub, name: e.target.value })}
                />
              </div>

              <div>
                <label className="block text-sm text-gray-600">Provider</label>
                <input
                  className="mt-1 px-3 py-2 border rounded-md w-full"
                  value={newSub.provider ?? ""}
                  onChange={(e) => setNewSub({ ...newSub, provider: e.target.value })}
                />
              </div>

              <div>
                <label className="block text-sm text-gray-600">Amount</label>
                <input
                  type="number"
                  className="mt-1 px-3 py-2 border rounded-md w-full"
                  value={newSub.amount ?? 0}
                  onChange={(e) => setNewSub({ ...newSub, amount: Number(e.target.value) })}
                />
              </div>

              <div>
                <label className="block text-sm text-gray-600">Frequency</label>
                <select
                  className="mt-1 px-3 py-2 border rounded-md w-full"
                  value={newSub.frequency as string}
                  onChange={(e) => setNewSub({ ...newSub, frequency: e.target.value as Frequency })}
                >
                  <option>Monthly</option>
                  <option>Yearly</option>
                  <option>Weekly</option>
                  <option>One-time</option>
                </select>
              </div>

              <div>
                <label className="block text-sm text-gray-600">Next Renewal</label>
                <input
                  type="date"
                  className="mt-1 px-3 py-2 border rounded-md w-full"
                  value={newSub.nextRenewal}
                  onChange={(e) => setNewSub({ ...newSub, nextRenewal: e.target.value })}
                />
              </div>

              <div>
                <label className="block text-sm text-gray-600">Category</label>
                <select
                  className="mt-1 px-3 py-2 border rounded-md w-full"
                  value={newSub.category}
                  onChange={(e) => setNewSub({ ...newSub, category: e.target.value })}
                >
                  <option>OTT</option>
                  <option>Utilities</option>
                  <option>Broadband</option>
                  <option>Recharge</option>
                  <option>Insurance</option>
                  <option>Other</option>
                </select>
              </div>

              <div className="md:col-span-2">
                <label className="block text-sm text-gray-600">Notes (optional)</label>
                <input
                  className="mt-1 px-3 py-2 border rounded-md w-full"
                  value={newSub.notes ?? ""}
                  onChange={(e) => setNewSub({ ...newSub, notes: e.target.value })}
                />
              </div>

              <div className="flex items-center gap-3 md:col-span-2 justify-end">
                <button
                  onClick={() => {
                    setShowAddModal(false);
                    setNewSub(null);
                  }}
                  className="px-4 py-2 border rounded-md"
                >
                  Cancel
                </button>
                <button onClick={saveNewSubscription} className="px-4 py-2 bg-indigo-600 text-white rounded-md">
                  Save Subscription
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
