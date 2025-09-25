import {  useState,useEffect } from "react";
import AuthContext from "./Authcontext";
import { jwtDecode } from "jwt-decode";

export default function AuthProvider({ children }) {
    const [user, setUser] = useState(null);


    // Obtener el token del localStorage y decodificarlo para obtener la información del usuario
    useEffect(() => {
        const token = localStorage.getItem('accessToken');
        if (token) {
            try {
                const decoded = jwtDecode(token);
                setUser(decoded);
            } catch {
                setUser(null);
            }
        }
    }, []);

    const login = (accessToken, refreshToken) => {
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', refreshToken);
        
        const decoded = jwtDecode(accessToken);

        setUser(decoded);
    }

    const logout = () => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');

        setUser(null);
    }

    return (
        <AuthContext.Provider value={{ user, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
}