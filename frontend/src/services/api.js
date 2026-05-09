import axios from 'axios';

const API_BASE = process.env.REACT_APP_API_URL || 'http://localhost:9000';
const api = axios.create({ baseURL: API_BASE });

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

export const userAPI = {
  register: (data) => api.post('/api/users/register', data),
  login: (data) => api.post('/api/users/login', data),
};
export const movieAPI = {
  getAll: () => api.get('/api/movies'),
  add: (data) => api.post('/api/movies', data),
};
export const bookingAPI = {
  create: (data) => api.post('/api/bookings', data),
  getByUser: (userId) => api.get(`/api/bookings/user/${userId}`),
};
export default api;
