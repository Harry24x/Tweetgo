import { useContext } from "react";
import { Navigate, Outlet } from "react-router-dom";
import { AuthContext } from "../Context/AuthContext";

export default function PrivateRoute() {
    const { token } = useContext(AuthContext);
    return token ? <Outlet /> : <Navigate to="/login" replace />;
}