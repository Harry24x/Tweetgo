import { useContext } from "react";
import { Navigate, Outlet } from "react-router-dom";
import { AuthContext } from "../Context/AuthContext";

export default function PrivateRoute({ children }) {
    const { token } = useContext(AuthContext);
    return token ? children : <Navigate to="/login" replace />;
}