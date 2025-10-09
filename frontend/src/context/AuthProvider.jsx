import { useState, useEffect } from "react";
import { jwtDecode } from "jwt-decode";
import AuthContext from "./AuthContext";

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [sessionExpired, setSessionExpired] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem("accessToken");
    if (token) {
      try {
        const decoded = jwtDecode(token);
        setUser(decoded);

        // Manejo de expiración
        if (decoded.exp) {
          const msToExpire = decoded.exp * 1000 - Date.now();
          if (msToExpire > 0) {
            const timeout = setTimeout(() => setSessionExpired(true), msToExpire);
            return () => clearTimeout(timeout);
          } else {
            setSessionExpired(true);
          }
        }
      } catch {
        setUser(null);
        setSessionExpired(true);
      }
    }
  }, []);

  const login = (accessToken, refreshToken) => {
    localStorage.setItem("accessToken", accessToken);
    localStorage.setItem("refreshToken", refreshToken);

    const decoded = jwtDecode(accessToken);
    setUser(decoded);

    // Manejo de expiración al hacer login
    if (decoded.exp) {
      const msToExpire = decoded.exp * 1000 - Date.now();
      if (msToExpire > 0) {
        setTimeout(() => setSessionExpired(true), msToExpire);
      } else {
        setSessionExpired(true);
      }
    }
  };

  const logout = () => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
    setUser(null);
    setSessionExpired(false);
  };

  return (
    <AuthContext.Provider
      value={{ user, login, logout, sessionExpired, setSessionExpired }}
    >
      {children}
    </AuthContext.Provider>
  );
}