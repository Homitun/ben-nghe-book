// TODO: Implement book list with search, pagination, and actions
export default function BookListPage() {
  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-xl font-semibold text-gray-900">Danh sách sách</h2>
        <a
          href="/books/new"
          className="bg-primary-600 hover:bg-primary-700 text-white text-sm font-medium px-4 py-2 rounded-lg"
        >
          + Thêm sách mới
        </a>
      </div>

      {/* TODO: Search bar */}
      {/* TODO: Book table */}
    </div>
  )
}
