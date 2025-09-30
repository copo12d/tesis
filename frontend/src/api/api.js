import axios from 'axios';
import { refreshToken } from '../service/RefreshToken';
import { Navigate } from 'react-router-dom';

const BASE_URL = 'http://localhost:8080/api/v1';

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
        
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');

        
        window.location.href = '/login';
        
      }


    }
    return Promise.reject(error);
  }
)



export default api;

