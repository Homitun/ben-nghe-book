import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { useAuthStore } from '@/store/authStore'
import MainLayout from '@/components/layout/MainLayout'
import LoginPage from '@/pages/LoginPage'
import DashboardPage from '@/pages/DashboardPage'
import BookListPage from '@/pages/books/BookListPage'
import BookFormPage from '@/pages/books/BookFormPage'
import InventoryPage from '@/pages/inventory/InventoryPage'
import HaravanPage from '@/pages/haravan/HaravanPage'
import UserManagementPage from '@/pages/admin/UserManagementPage'

function RequireAuth({ children }: { children: React.ReactNode }) {
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated)
  return isAuthenticated ? <>{children}</> : <Navigate to="/login" replace />
}

function RequireAdmin({ children }: { children: React.ReactNode }) {
  const user = useAuthStore((s) => s.user)
  return user?.role === 'ADMIN' ? <>{children}</> : <Navigate to="/" replace />
}

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />

        <Route
          path="/"
          element={
            <RequireAuth>
              <MainLayout />
            </RequireAuth>
          }
        >
          <Route index element={<DashboardPage />} />
          <Route path="books" element={<BookListPage />} />
          <Route path="books/new" element={<BookFormPage />} />
          <Route path="books/:id/edit" element={<BookFormPage />} />
          <Route path="inventory" element={<InventoryPage />} />
          <Route path="haravan" element={<HaravanPage />} />
          <Route
            path="admin/users"
            element={
              <RequireAdmin>
                <UserManagementPage />
              </RequireAdmin>
            }
          />
        </Route>

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  )
}
