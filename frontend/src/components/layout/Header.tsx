import { useNavigate } from 'react-router-dom'
import { useAuthStore } from '@/store/authStore'

export default function Header() {
  const { user, logout } = useAuthStore()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <header className="bg-white border-b border-gray-200 px-6 py-3 flex items-center justify-between">
      <div />
      <div className="flex items-center gap-4">
        <span className="text-sm text-gray-600">
          Xin chào, <span className="font-medium text-gray-900">{user?.fullName}</span>
        </span>
        <button
          onClick={handleLogout}
          className="text-sm text-red-600 hover:text-red-700 font-medium"
        >
          Đăng xuất
        </button>
      </div>
    </header>
  )
}
