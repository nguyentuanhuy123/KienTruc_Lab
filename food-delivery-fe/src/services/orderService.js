import axios from "axios";

const API = "http://localhost:8082/orders";

export const getOrders = () => axios.get(API);
export const getOrderById = (id) => axios.get(`${API}/${id}`);
export const createOrder = (data) => axios.post(API, data);