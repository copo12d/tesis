import { Navigate } from "react-router-dom";
import { useContext } from "react";
import AuthContext from "../context/Authcontext";


export default function PublicRoute({ children }) {
    const { user } = useContext(AuthContext);
    return user ? <Navigate to="/" /> : children;
}