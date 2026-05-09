import React, { createContext, useContext, useState, useEffect } from 'react';
const AuthContext = createContext(null);
export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) setUser({
      token,
      username: localStorage.getItem('username'),
      userId: localStorage.getItem('userId'),
      fullName: localStorage.getItem('fullName'),
    });
  }, []);
  const login = (data) => {
    localStorage.setItem('token', data.token);
    localStorage.setItem('username', data.username);
    localStorage.setItem('userId', data.userId);
    localStorage.setItem('fullName', data.fullName || '');
    setUser(data);
  };
  const logout = () => {
    ['token','username','userId','fullName'].forEach(k => localStorage.removeItem(k));
    setUser(null);
  };
  return <AuthContext.Provider value={{ user, login, logout }}>{children}</AuthContext.Provider>;
};
export const useAuth = () => useContext(AuthContext);
