import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Loginpage from "./Pages/Loginpage";
import Registerpage from "./Pages/Registerpage";
import Homepage from "./Pages/Homepage";

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<Homepage />} />
                <Route path="/login" element={<Loginpage />} />
                <Route path="/register" element={<Registerpage />} />
            </Routes>
        </Router>
    );
}

export default App;
