import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { userAPI } from '../services/api';

export default function RegisterPage() {
  const [form, setForm] = useState({ username: '', email: '', password: '', fullName: '' });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true); setError(''); setSuccess('');
    try {
      await userAPI.register(form);
      setSuccess('Đăng ký thành công! Đang chuyển đến đăng nhập...');
      setTimeout(() => navigate('/login'), 1500);
    } catch (err) {
      setError(err.response?.data?.error || 'Đăng ký thất bại');
    } finally { setLoading(false); }
  };

  const field = (key, placeholder, type='text') => (
    <input style={styles.input} type={type} placeholder={placeholder}
      value={form[key]} onChange={e => setForm({...form, [key]: e.target.value})} required />
  );

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <h2 style={styles.title}>🎬 Đăng ký tài khoản</h2>
        {error && <div style={styles.error}>{error}</div>}
        {success && <div style={styles.success}>{success}</div>}
        <form onSubmit={handleSubmit}>
          {field('fullName', 'Họ và tên')}
          {field('username', 'Username')}
          {field('email', 'Email', 'email')}
          {field('password', 'Mật khẩu', 'password')}
          <button style={styles.btn} disabled={loading}>
            {loading ? 'Đang đăng ký...' : 'Đăng ký'}
          </button>
        </form>
        <p style={styles.link}>Đã có tài khoản? <Link to="/login">Đăng nhập</Link></p>
      </div>
    </div>
  );
}

const styles = {
  container: { minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', background: '#1a1a2e' },
  card: { background: '#fff', borderRadius: 12, padding: 40, width: 380, boxShadow: '0 8px 32px rgba(0,0,0,0.3)' },
  title: { textAlign: 'center', color: '#e94560', marginBottom: 24 },
  input: { width: '100%', padding: '12px 16px', marginBottom: 16, borderRadius: 8, border: '1px solid #ddd', fontSize: 15, boxSizing: 'border-box' },
  btn: { width: '100%', padding: 14, background: '#e94560', color: '#fff', border: 'none', borderRadius: 8, fontSize: 16, cursor: 'pointer', fontWeight: 600 },
  error: { background: '#fff0f0', color: '#e94560', padding: 12, borderRadius: 8, marginBottom: 16, fontSize: 14 },
  success: { background: '#f0fff4', color: '#27ae60', padding: 12, borderRadius: 8, marginBottom: 16, fontSize: 14 },
  link: { textAlign: 'center', marginTop: 16, color: '#666' },
};
