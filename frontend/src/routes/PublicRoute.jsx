import { Navigate, useLocation } from "react-router-dom";
import { useContext } from "react";
import AuthContext from "../context/AuthContext";

export default function PublicRoute({ children }) {
    const { user } = useContext(AuthContext);
    const location = useLocation();

    // Si hay usuario, redirige a la ruta previa (si existe), si no:
    // Si la ruta actual es /mobile/login, redirige a /mobile/containers/collect
    // Si no, redirige al dashboard
    if (user) {
        if (location.pathname.startsWith("/mobile")) {
            return <Navigate to="/mobile/containers/collect" replace />;
        }
        const from = location.state?.from || "/";
        return <Navigate to={from} replace />;
    }
    return children;
}