import { NavLink } from 'react-router-dom'

const links = [
  { to: '/dashboard', label: 'Dashboard' },
  { to: '/income', label: 'Income' },
  { to: '/expenses', label: 'Expenses' },
  { to: '/budgets', label: 'Budgets' },
  { to: '/savings', label: 'Savings Goals' },
  { to: '/reports', label: 'Reports' },
  { to: '/profile', label: 'Profile' }
]

export default function Sidebar() {
  return (
    <aside className="hidden w-56 shrink-0 border-r border-ledger-100 bg-white/60 p-4 md:block">
      <nav className="flex flex-col gap-1">
        {links.map((link) => (
          <NavLink
            key={link.to}
            to={link.to}
            className={({ isActive }) =>
              `rounded-md px-3 py-2 text-sm transition-colors ${
                isActive ? 'bg-ledger-500 text-white' : 'text-ink/70 hover:bg-ledger-50'
              }`
            }
          >
            {link.label}
          </NavLink>
        ))}
      </nav>
    </aside>
  )
}
