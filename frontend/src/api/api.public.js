import axios from 'axios';

const BASE_URL = import.meta.env.VITE_API_URL;

const apiPublic = axios.create({
  baseURL: `${BASE_URL}`,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: false,
});

// No agregues ningún interceptor de autenticación ni refresh

export default apiPublic;

