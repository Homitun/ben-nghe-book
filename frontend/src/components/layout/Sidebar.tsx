import { NavLink } from 'react-router-dom'
import { useAuthStore } from '@/store/authStore'

const navItems = [
  { to: '/',              label: 'Dashboard',    icon: '📊', always: true },
  { to: '/books',         label: 'Danh sách sách', icon: '📚', always: true },
  { to: '/inventory',    label: 'Nhập kho',      icon: '📦', perm: 'INVENTORY_CREATE' },
  { to: '/haravan',      label: 'Haravan',       icon: '🔄', perm: 'HARAVAN_IMPORT' },
  { to: '/admin/users',  label: 'Người dùng',    icon: '👥', role: 'ADMIN' },
]

export default function Sidebar() {
  const { user, hasPermission } = useAuthStore()

  const visible = navItems.filter((item) => {
    if (item.always) return true
    if (item.role) return user?.role === item.role
    if (item.perm) return hasPermission(item.perm)
    return true
  })

  return (
    <aside className="w-60 bg-primary-900 text-white flex flex-col">
      {/* Logo */}
      <div className="px-6 py-5 border-b border-primary-700">
        <h1 className="text-xl font-bold tracking-wide">Bến Nghé</h1>
        <p className="text-xs text-primary-300 mt-0.5">Quản lý sách</p>
      </div>

      {/* Nav */}
      <nav className="flex-1 px-3 py-4 space-y-1">
        {visible.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            end={item.to === '/'}
            className={({ isActive }) =>
              `flex items-center gap-3 rounded-lg px-4 py-2.5 text-sm font-medium transition-colors ${
                isActive
                  ? 'bg-primary-600 text-white'
                  : 'text-primary-200 hover:bg-primary-700 hover:text-white'
              }`
            }
          >
            <span>{item.icon}</span>
            {item.label}
          </NavLink>
        ))}
      </nav>

      {/* User info */}
      <div className="px-4 py-4 border-t border-primary-700 text-sm text-primary-300">
        <p className="font-medium text-white">{user?.fullName}</p>
        <p className="text-xs">{user?.role}</p>
      </div>
    </aside>
  )
}
