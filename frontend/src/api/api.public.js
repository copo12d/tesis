import axios from 'axios';

const BASE_URL =
  import.meta?.env?.VITE_API_BASE_URL ||
  import.meta?.env?.VITE_API_URL ||
  'http://localhost:8080';

if (!BASE_URL) {
  // Último fallback defensivo (normalmente no llega aquí)
  console.warn('BASE_URL no definida. Usando http://localhost:8080');
}

const apiPublic = axios.create({
  baseURL: BASE_URL,
  headers: { 'Content-Type': 'application/json' },
  withCredentials: false,
});

export default apiPublic;

