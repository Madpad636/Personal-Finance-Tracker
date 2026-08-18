import { test, expect } from '@playwright/test'

// End-to-end smoke test for the register -> dashboard flow.
// TODO: extend with add-income, add-expense, create-budget, savings-goal,
// generate-report, update-profile, logout, per the full test plan in
// docs/TEST_PLAN.md.
test('a new user can register and land on the dashboard', async ({ page }) => {
  await page.goto('/register')

  const email = `e2e-${Date.now()}@example.com`
  await page.getByLabel('Full name').fill('E2E Test User')
  await page.getByLabel('Email').fill(email)
  await page.getByLabel('Password').fill('Str0ng!Pass')
  await page.getByRole('button', { name: /create account/i }).click()

  await expect(page).toHaveURL(/\/dashboard/)
})

test('an unauthenticated user is redirected to login', async ({ page }) => {
  await page.goto('/dashboard')
  await expect(page).toHaveURL(/\/login/)
})
