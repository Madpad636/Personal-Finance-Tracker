import axios from 'axios'

/**
 * Central Axios instance. Attaches the access token to every request and,
 * on a 401, attempts a single silent refresh before giving up and forcing
 * a re-login. Keep all HTTP concerns (base URL, interceptors) here so
 * feature services stay thin.
 */
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api',
  headers: { 'Content-Type': 'application/json' }
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

let isRefreshing = false
let pendingQueue: Array<() => void> = []

function clearAuthAndRedirect() {
  localStorage.removeItem('accessToken')
  localStorage.removeItem('refreshToken')
  localStorage.removeItem('authUser')
  window.location.href = '/login'
}

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config
    if ([401, 403].includes(error.response?.status) && !originalRequest._retry) {
      originalRequest._retry = true

      if (isRefreshing) {
        // Queue this request until the in-flight refresh resolves.
        return new Promise((resolve) => {
          pendingQueue.push(() => resolve(api(originalRequest)))
        })
      }

      isRefreshing = true
      try {
        const refreshToken = localStorage.getItem('refreshToken')
        if (!refreshToken) throw new Error('No refresh token available')

        const { data } = await axios.post(
          `${api.defaults.baseURL}/auth/refresh`,
          { refreshToken }
        )
        localStorage.setItem('accessToken', data.accessToken)
        localStorage.setItem('refreshToken', data.refreshToken)

        pendingQueue.forEach((cb) => cb())
        pendingQueue = []

        return api(originalRequest)
      } catch (refreshError) {
        clearAuthAndRedirect()
        return Promise.reject(refreshError)
      } finally {
        isRefreshing = false
      }
    }
    return Promise.reject(error)
  }
)

export default api
