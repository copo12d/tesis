import axios from 'axios';
import { Navigate } from 'react-router-dom';

const BASE_URL = 'http://localhost:8080';

const api = axios.create({
  baseURL: `${BASE_URL}`,
  headers: {
    'Content-Type': 'application/json',
  },
});

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

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const refreshToken = localStorage.getItem('refreshToken');
        
        if (!refreshToken) throw new Error('No refresh token available');

        const { data } = await axios.post(`${BASE_URL}/auth/refresh`, { token: refreshToken });

        localStorage.setItem('accessToken', data.accessToken);

        api.defaults.headers.Authorization = `Bearer ${data.accessToken}`;
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

