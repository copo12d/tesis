import axios from 'axios';
import { refreshToken } from '../service/RefreshToken';

const BASE_URL = import.meta.env.VITE_API_URL ||
  import.meta.env.VITE_API_BASE_URL;

const api = axios.create({
  baseURL: `${BASE_URL}`,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true,
});
// --- REQUEST INTERCEPTOR --- //
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
)

// --- RESPONSE INTERCEPTOR --- //
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const newAccessToken = await refreshToken();
        api.defaults.headers.common['Authorization'] = `Bearer ${newAccessToken}`;
        originalRequest.headers['Authorization'] = `Bearer ${newAccessToken}`;

        return api(originalRequest);
      } catch {
        window.sessionExpired = true; // O usa un event emitter
        return Promise.reject(error);
      }
    }
    return Promise.reject(error);
  }
)



export default api;

