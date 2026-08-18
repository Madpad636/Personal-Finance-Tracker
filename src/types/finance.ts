export interface Income {
  id: string
  amount: number
  source: string
  date: string // ISO date
  description?: string
}

export interface Expense {
  id: string
  amount: number
  categoryId: string
  categoryName: string
  date: string
  description?: string
}

export interface Budget {
  id: string
  categoryId: string | null
  categoryName: string | null
  month: number
  year: number
  limitAmount: number
  spentAmount: number
  remainingAmount: number
  percentUsed: number
  exceeded: boolean
}

export interface SavingsGoal {
  id: string
  name: string
  targetAmount: number
  currentAmount: number
  remainingAmount: number
  percentComplete: number
  targetDate?: string
}

export interface DashboardSummary {
  totalIncome: number
  totalExpenses: number
  remainingBalance: number
  monthlyBudget: number
  totalSavings: number
  spendingByCategory: { category: string; amount: number }[]
  monthlyTrend: { month: string; income: number; expenses: number }[]
}
