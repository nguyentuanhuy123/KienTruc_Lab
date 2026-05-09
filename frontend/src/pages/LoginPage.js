import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { userAPI } from '../services/api';
import { useAuth } from '../context/AuthContext';

export default function LoginPage() {
  const [form, setForm] = useState({ username: '', password: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true); setError('');
    try {
      const res = await userAPI.login(form);
      login(res.data);
      navigate('/movies');
    } catch (err) {
      setError(err.response?.data?.error || 'Đăng nhập thất bại');
    } finally { setLoading(false); }
  };

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <h2 style={styles.title}>🎬 Đăng nhập</h2>
        {error && <div style={styles.error}>{error}</div>}
        <form onSubmit={handleSubmit}>
          <input style={styles.input} placeholder="Username" value={form.username}
            onChange={e => setForm({...form, username: e.target.value})} required />
          <input style={styles.input} type="password" placeholder="Mật khẩu"
            value={form.password}
            onChange={e => setForm({...form, password: e.target.value})} required />
          <button style={styles.btn} disabled={loading}>
            {loading ? 'Đang đăng nhập...' : 'Đăng nhập'}
          </button>
        </form>
        <p style={styles.link}>Chưa có tài khoản? <Link to="/register">Đăng ký ngay</Link></p>
      </div>
    </div>
  );
}

const styles = {
  container: { minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', background: '#1a1a2e' },
  card: { background: '#fff', borderRadius: 12, padding: 40, width: 360, boxShadow: '0 8px 32px rgba(0,0,0,0.3)' },
  title: { textAlign: 'center', color: '#e94560', marginBottom: 24 },
  input: { width: '100%', padding: '12px 16px', marginBottom: 16, borderRadius: 8, border: '1px solid #ddd', fontSize: 15, boxSizing: 'border-box' },
  btn: { width: '100%', padding: 14, background: '#e94560', color: '#fff', border: 'none', borderRadius: 8, fontSize: 16, cursor: 'pointer', fontWeight: 600 },
  error: { background: '#fff0f0', color: '#e94560', padding: 12, borderRadius: 8, marginBottom: 16, fontSize: 14 },
  link: { textAlign: 'center', marginTop: 16, color: '#666' },
};
