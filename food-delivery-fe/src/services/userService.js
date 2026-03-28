import axios from "axios";

const API = "http://localhost:8081/users";

export const getUserById = (id) => axios.get(`${API}/${id}`);
export const createUser = (data) => axios.post(API, data);