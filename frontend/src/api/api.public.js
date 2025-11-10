import axios from 'axios';

const BASE_URL =  import.meta.env.VITE_API_URL ||
  import.meta.env.VITE_API_BASE_URL;

const apiPublic = axios.create({
  baseURL: BASE_URL,
  headers: { 'Content-Type': 'application/json' },
  withCredentials: false,
});

export default apiPublic;

