// TODO: Implement dashboard with charts
export default function DashboardPage() {
  return (
    <div>
      <h2 className="text-xl font-semibold text-gray-900 mb-6">Dashboard</h2>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <StatCard label="Sách đã bán" value="—" />
        <StatCard label="Sách đã nhập" value="—" />
        <StatCard label="Tổng SKU" value="—" />
      </div>
      {/* TODO: Add Recharts charts here */}
    </div>
  )
}

function StatCard({ label, value }: { label: string; value: string }) {
  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-5">
      <p className="text-sm text-gray-500">{label}</p>
      <p className="text-3xl font-bold text-gray-900 mt-1">{value}</p>
    </div>
  )
}
