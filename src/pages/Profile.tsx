import { useEffect, useState, type FormEvent } from 'react'
import { userProfileService, type UserProfile } from '@/services/userProfileService'
import { authService } from '@/services/authService'
import Loader from '@/components/common/Loader'
import { useToast } from '@/components/common/Toast'

export default function Profile() {
  const { notify } = useToast()
  const [profile, setProfile] = useState<UserProfile | null>(null)
  const [loading, setLoading] = useState(true)
  const [fullName, setFullName] = useState('')
  const [savingProfile, setSavingProfile] = useState(false)

  const [currentPassword, setCurrentPassword] = useState('')
  const [newPassword, setNewPassword] = useState('')
  const [savingPassword, setSavingPassword] = useState(false)

  useEffect(() => {
    userProfileService
      .get()
      .then((p) => {
        setProfile(p)
        setFullName(p.fullName)
      })
      .catch(() => notify('error', 'Could not load profile.'))
      .finally(() => setLoading(false))
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const handleProfileSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setSavingProfile(true)
    try {
      const updated = await userProfileService.update(fullName)
      setProfile(updated)
      notify('success', 'Profile updated.')
    } catch {
      notify('error', 'Could not update profile.')
    } finally {
      setSavingProfile(false)
    }
  }

  const handlePasswordSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setSavingPassword(true)
    try {
      await authService.changePassword(currentPassword, newPassword)
      notify('success', 'Password changed.')
      setCurrentPassword('')
      setNewPassword('')
    } catch {
      notify('error', 'Could not change password. Check your current password.')
    } finally {
      setSavingPassword(false)
    }
  }

  if (loading) return <Loader label="Loading profile…" />

  return (
    <div className="max-w-lg space-y-6">
      <h1 className="font-display text-2xl text-ink">Profile</h1>

      <form onSubmit={handleProfileSubmit} className="rounded-lg bg-white p-6 shadow-sm">
        <p className="font-display text-lg text-ink">Account details</p>
        <label className="mt-4 block text-sm text-ink/70">
          Full name
          <input
            required
            value={fullName}
            onChange={(e) => setFullName(e.target.value)}
            className="mt-1 w-full rounded-md border border-ledger-100 px-3 py-2 text-sm focus:border-ledger-500 focus:outline-none"
          />
        </label>
        <label className="mt-4 block text-sm text-ink/70">
          Email
          <input
            disabled
            value={profile?.email ?? ''}
            className="mt-1 w-full cursor-not-allowed rounded-md border border-ledger-100 bg-ledger-50 px-3 py-2 text-sm text-ink/50"
          />
        </label>
        <button
          type="submit"
          disabled={savingProfile}
          className="mt-5 rounded-md bg-ledger-500 px-4 py-2 text-sm text-white hover:bg-ledger-600 disabled:opacity-60"
        >
          {savingProfile ? 'Saving…' : 'Save changes'}
        </button>
      </form>

      <form onSubmit={handlePasswordSubmit} className="rounded-lg bg-white p-6 shadow-sm">
        <p className="font-display text-lg text-ink">Change password</p>
        <label className="mt-4 block text-sm text-ink/70">
          Current password
          <input
            type="password" required
            value={currentPassword}
            onChange={(e) => setCurrentPassword(e.target.value)}
            className="mt-1 w-full rounded-md border border-ledger-100 px-3 py-2 text-sm focus:border-ledger-500 focus:outline-none"
          />
        </label>
        <label className="mt-4 block text-sm text-ink/70">
          New password
          <input
            type="password" required minLength={8}
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
            className="mt-1 w-full rounded-md border border-ledger-100 px-3 py-2 text-sm focus:border-ledger-500 focus:outline-none"
          />
        </label>
        <button
          type="submit"
          disabled={savingPassword}
          className="mt-5 rounded-md bg-ledger-500 px-4 py-2 text-sm text-white hover:bg-ledger-600 disabled:opacity-60"
        >
          {savingPassword ? 'Updating…' : 'Update password'}
        </button>
      </form>
    </div>
  )
}
