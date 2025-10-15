import axios from "axios";

const BASE_URL = import.meta.env.VITE_API_URL;

export async function refreshToken() {
    const refreshToken = localStorage.getItem('refreshToken');

    if (!refreshToken) throw new Error('No refresh token available');
    try {
        const { data } = await axios.post(`${BASE_URL}/auth/refresh`, { token: refreshToken });
        localStorage.setItem('accessToken', data.accessToken);
        if(data.refreshToken) {
            localStorage.setItem('refreshToken', data.refreshToken);
        }
        return data.accessToken;
    } catch (error) {
        console.error('Failed to refresh token', error);
        throw error;
    }
}