import axios from 'axios'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api/v1'

export const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Intercept request to inject JWT token
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('crmpro_access_token')
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Intercept response for 401 unauthorized handling
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      const refreshToken = localStorage.getItem('crmpro_refresh_token')
      if (refreshToken && !error.config._retry) {
        error.config._retry = true
        try {
          const res = await axios.post(`${API_BASE_URL}/auth/refresh`, { refreshToken })
          const newAccessToken = res.data.data.accessToken
          localStorage.setItem('crmpro_access_token', newAccessToken)
          error.config.headers.Authorization = `Bearer ${newAccessToken}`
          return api(error.config)
        } catch (refreshErr) {
          localStorage.removeItem('crmpro_access_token')
          localStorage.removeItem('crmpro_refresh_token')
          localStorage.removeItem('crmpro_user')
          window.location.href = '/login'
        }
      }
    }
    return Promise.reject(error)
  }
)
