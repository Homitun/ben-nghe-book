// ─── Auth ────────────────────────────────────────────────────────────────────
export interface AuthUser {
  userId: number
  username: string
  fullName: string
  role: 'ADMIN' | 'MANAGER' | 'STAFF'
  permissions: string[]
  token: string
}

// ─── Common ──────────────────────────────────────────────────────────────────
export interface ApiResponse<T> {
  success: boolean
  message?: string
  data: T
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

// ─── Book ────────────────────────────────────────────────────────────────────
export interface Book {
  id: number
  sku: string
  isbn?: string
  title: string
  publisherName?: string
  publicationYear?: number
  description?: string
  coverImageUrl?: string
  warehouse?: string
  shelf?: string
  importPrice: number
  salePrice: number
  coverPrice?: number
  pageCount?: number
  weightGram?: number
  size?: string
  targetAudience?: string
  distributor?: string
  translator?: string
  authors: string[]
  currentStock?: number
  createdAt: string
  updatedAt: string
}

// ─── User ────────────────────────────────────────────────────────────────────
export interface User {
  id: number
  username: string
  fullName: string
  email?: string
  roleName: string
  isActive: boolean
  createdAt: string
}

export interface Role {
  id: number
  name: string
  description?: string
}

// ─── Inventory ───────────────────────────────────────────────────────────────
export interface InventoryReceipt {
  id: number
  receiptCode: string
  receiptDate: string
  importedBy: string
  notes?: string
  importSource: 'MANUAL' | 'EXCEL'
  items: InventoryReceiptItem[]
}

export interface InventoryReceiptItem {
  id: number
  bookId: number
  bookTitle: string
  sku: string
  quantity: number
  importPrice: number
}

// ─── Dashboard ───────────────────────────────────────────────────────────────
export interface DashboardSummary {
  totalSold: number
  topSellingBooks: TopBook[]
  mostStocked: StockItem[]
  leastStocked: StockItem[]
  dailySales: DailySale[]
}

export interface TopBook {
  bookId: number
  sku: string
  title: string
  totalSold: number
}

export interface StockItem {
  bookId: number
  sku: string
  title: string
  totalImported: number
  totalSold: number
  currentStock: number
}

export interface DailySale {
  date: string
  quantity: number
}
