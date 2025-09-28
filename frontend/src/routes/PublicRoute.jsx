import { Navigate } from "react-router-dom";
import { useContext } from "react";
import AuthContext from "../context/Authcontext";
import { Dashboard } from "../pages/Dashboard";

export default function PublicRoute({ children }) {
    const { user } = useContext(AuthContext);
    return user ? <Navigate to="/" /> : children;
}