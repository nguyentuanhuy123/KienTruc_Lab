import React from "react";
import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";
import Users from "./pages/Users";
import Orders from "./pages/Orders";
import "bootstrap/dist/css/bootstrap.min.css";

function App() {
  return (
    <Router>
      <div className="container mt-3">
        <Link to="/users" className="btn btn-primary me-2">Users</Link>
        <Link to="/orders" className="btn btn-success">Orders</Link>
      </div>

      <Routes>
        <Route path="/users" element={<Users />} />
        <Route path="/orders" element={<Orders />} />
      </Routes>
    </Router>
  );
}

export default App;