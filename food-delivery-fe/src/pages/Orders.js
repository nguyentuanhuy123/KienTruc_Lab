import React, { useState } from "react";
import { getOrderById, createOrder } from "../services/orderService";

export default function Orders() {
  const [id, setId] = useState("");
  const [order, setOrder] = useState(null);

  const [form, setForm] = useState({
    userId: "",
    status: "PENDING",
    totalAmount: ""
  });

  const handleGet = async () => {
    const res = await getOrderById(id);
    setOrder(res.data);
  };

  const handleCreate = async () => {
    await createOrder(form);
    alert("Order created!");
  };

  return (
    <div className="container mt-4">
      <h2>Order Service</h2>

      <input
        placeholder="Enter order ID"
        onChange={(e) => setId(e.target.value)}
      />
      <button onClick={handleGet}>Get Order</button>

      {order && (
        <div>
          <h4>Order #{order.id}</h4>
          <p>Status: {order.status}</p>
          <p>Total: {order.totalAmount}</p>
          <p>User: {order.user?.name}</p>
        </div>
      )}

      <hr />

      <h3>Create Order</h3>

      <input placeholder="User ID" onChange={(e) => setForm({...form, userId: e.target.value})} />
      <input placeholder="Total" onChange={(e) => setForm({...form, totalAmount: e.target.value})} />

      <button onClick={handleCreate}>Create</button>
    </div>
  );
}