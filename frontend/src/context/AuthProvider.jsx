import { useState, useEffect } from "react";
import { jwtDecode } from "jwt-decode";
import AuthContext from "./AuthContext";

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [sessionExpired, setSessionExpired] = useState(false);

  // Función para validar token y actualizar estado
  const checkToken = () => {
    const token = localStorage.getItem("accessToken");
    if (token) {
      try {
        const decoded = jwtDecode(token);
        setUser(decoded);

        if (decoded.exp) {
          const msToExpire = decoded.exp * 1000 - Date.now();
          if (msToExpire > 0) {
            setSessionExpired(false);
            // Set timeout para expirar sesión
            setTimeout(() => setSessionExpired(true), msToExpire);
          } else {
            setSessionExpired(true);
            setUser(null);
          }
        } else {
          setSessionExpired(true);
          setUser(null);
        }
      } catch {
        setSessionExpired(true);
        setUser(null);
      }
    } else {
      setSessionExpired(true);
      setUser(null);
    }
  };

  useEffect(() => {
    checkToken();

    // Escucha cambios en el storage (por si borran el token en otra pestaña)
    const onStorage = () => checkToken();
    window.addEventListener("storage", onStorage);

    return () => window.removeEventListener("storage", onStorage);
  }, []);

  const login = (accessToken, refreshToken) => {
    localStorage.setItem("accessToken", accessToken);
    localStorage.setItem("refreshToken", refreshToken);
    checkToken();
  };

  const logout = () => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
    setUser(null);
    setSessionExpired(true);
  };

  return (
    <AuthContext.Provider
      value={{ user, login, logout, sessionExpired, setSessionExpired }}
    >
      {children}
    </AuthContext.Provider>
  );
}