import { Link } from 'react-router-dom'

export default function NotFound() {
  return (
    <div className="flex min-h-screen flex-col items-center justify-center gap-3 bg-paper text-center">
      <h1 className="font-display text-4xl text-ledger-600">404</h1>
      <p className="text-ink/60">This page doesn't exist.</p>
      <Link to="/dashboard" className="text-ledger-500 hover:underline">Back to dashboard</Link>
    </div>
  )
}
