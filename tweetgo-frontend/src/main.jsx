import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App.jsx";


import "bootstrap/dist/css/bootstrap.min.css";
import "./index.css";  // you can keep any custom overrides here


ReactDOM.createRoot(document.getElementById("root")).render(
    <React.StrictMode>
        <App />
    </React.StrictMode>
);

