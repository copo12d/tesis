import { Navigate, useLocation } from "react-router-dom";
import { useContext } from "react";
import AuthContext from "../context/AuthContext";

export default function PublicRoute({ children }) {
  const { user } = useContext(AuthContext);
  const location = useLocation();

  if (user) {
    // Mantener la misma URL si es flujo móvil (la escaneada con el QR)
    if (location.pathname.startsWith("/mobile")) {
      return children;
    }
    const from = location.state?.from || "/";
    return <Navigate to={from} replace />;
  }
  return children;
}