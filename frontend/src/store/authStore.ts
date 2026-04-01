import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import { AuthUser } from '@/types'

interface AuthState {
  user: AuthUser | null
  isAuthenticated: boolean
  login: (user: AuthUser) => void
  logout: () => void
  hasPermission: (permission: string) => boolean
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      user: null,
      isAuthenticated: false,

      login: (user) => {
        localStorage.setItem('token', user.token)
        set({ user, isAuthenticated: true })
      },

      logout: () => {
        localStorage.removeItem('token')
        set({ user: null, isAuthenticated: false })
      },

      hasPermission: (permission) => {
        const user = get().user
        if (!user) return false
        if (user.role === 'ADMIN') return true
        return user.permissions.includes(permission)
      },
    }),
    { name: 'auth-storage', partialize: (s) => ({ user: s.user, isAuthenticated: s.isAuthenticated }) }
  )
)
