import { useContext } from "react";
import { Navigate, useLocation } from "react-router-dom";
import AuthContext from "../context/AuthContext";

const PrivateRoute = ({ children }) => {
    const { user } = useContext(AuthContext);
    const location = useLocation();
    const token = localStorage.getItem("accessToken");

    if (!user || !token) {
        // Si la ruta es móvil, redirige al login móvil
        if (location.pathname.startsWith("/mobile")) {
            return <Navigate to="/mobile/login" replace />;
        }
        // Si no, redirige al login normal
        return <Navigate to="/login" replace />;
    }

    return children;
};

export default PrivateRoute;
