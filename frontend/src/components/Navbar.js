import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const handleLogout = () => { logout(); navigate('/login'); };

  return (
    <nav style={styles.nav}>
      <Link to="/movies" style={styles.brand}>🎬 MovieTicket</Link>
      <div style={styles.links}>
        <Link to="/movies" style={styles.link}>Phim</Link>
        <Link to="/bookings" style={styles.link}>Đơn của tôi</Link>
        {user && (
          <>
            <span style={styles.user}>👤 {user.fullName || user.username}</span>
            <button style={styles.logoutBtn} onClick={handleLogout}>Đăng xuất</button>
          </>
        )}
      </div>
    </nav>
  );
}

const styles = {
  nav: { background: '#0f3460', padding: '0 24px', height: 60, display: 'flex', alignItems: 'center', justifyContent: 'space-between' },
  brand: { color: '#e94560', fontWeight: 700, fontSize: 20, textDecoration: 'none' },
  links: { display: 'flex', alignItems: 'center', gap: 20 },
  link: { color: '#fff', textDecoration: 'none', fontSize: 14 },
  user: { color: '#74b9ff', fontSize: 14 },
  logoutBtn: { background: 'transparent', color: '#e94560', border: '1px solid #e94560', borderRadius: 6, padding: '6px 14px', cursor: 'pointer', fontSize: 13 },
};
