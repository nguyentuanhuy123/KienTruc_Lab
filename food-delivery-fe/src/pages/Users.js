import React, { useState } from "react";
import { createUser, getUserById } from "../services/userService";

export default function Users() {
  const [id, setId] = useState("");
  const [user, setUser] = useState(null);

  const [form, setForm] = useState({
    name: "",
    email: "",
    age: "",
    gender: "male",
    address: "",
    phone: ""
  });

  const handleGet = async () => {
    const res = await getUserById(id);
    setUser(res.data);
  };

  const handleCreate = async () => {
    await createUser(form);
    alert("Created!");
  };

  return (
    <div className="container mt-4">
      <h2>User Service</h2>

      <input
        placeholder="Enter user ID"
        onChange={(e) => setId(e.target.value)}
      />
      <button onClick={handleGet}>Get User</button>

      {user && (
        <div>
          <h4>{user.name}</h4>
          <p>{user.email}</p>
          <p>{user.gender}</p>
        </div>
      )}

      <hr />

      <h3>Create User</h3>

      <input placeholder="Name" onChange={(e) => setForm({...form, name: e.target.value})} />
      <input placeholder="Email" onChange={(e) => setForm({...form, email: e.target.value})} />
      <input placeholder="Age" onChange={(e) => setForm({...form, age: e.target.value})} />

      <select onChange={(e) => setForm({...form, gender: e.target.value})}>
        <option value="male">Male</option>
        <option value="female">Female</option>
      </select>

      <input placeholder="Address" onChange={(e) => setForm({...form, address: e.target.value})} />
      <input placeholder="Phone" onChange={(e) => setForm({...form, phone: e.target.value})} />

      <button onClick={handleCreate}>Create</button>
    </div>
  );
}