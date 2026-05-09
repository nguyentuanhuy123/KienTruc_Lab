import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { bookingAPI } from '../services/api';
import { useAuth } from '../context/AuthContext';
import Navbar from '../components/Navbar';

export default function BookingPage() {
  const { state } = useLocation();
  const { user } = useAuth();
  const navigate = useNavigate();
  const [seats, setSeats] = useState(1);
  const [status, setStatus] = useState('idle'); // idle | loading | success | error
  const [booking, setBooking] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!user) navigate('/login');
    if (!state?.movie) navigate('/movies');
  }, [user, state, navigate]);

  const movie = state?.movie;
  if (!movie) return null;

  const total = movie.price * seats;

  const handleBook = async () => {
    setStatus('loading'); setError('');
    try {
      const res = await bookingAPI.create({
        userId: user.userId,
        username: user.username,
        movieId: movie.id,
        movieTitle: movie.title,
        seats: seats,
        pricePerSeat: movie.price,
      });
      setBooking(res.data);
      setStatus('success');
    } catch (err) {
      setError(err.response?.data?.error || 'Đặt vé thất bại');
      setStatus('error');
    }
  };

  if (status === 'success') return (
    <div style={styles.page}>
      <Navbar />
      <div style={styles.center}>
        <div style={styles.successCard}>
          <div style={{ fontSize: 60 }}>✅</div>
          <h2 style={{ color: '#27ae60' }}>Đặt vé thành công!</h2>
          <p style={{ color: '#555' }}>Booking #{booking.id} đã được tạo</p>
          <p style={{ color: '#555' }}>Trạng thái: <strong style={{ color: '#e67e22' }}>{booking.status}</strong></p>
          <p style={{ color: '#888', fontSize: 13 }}>⏳ Payment đang xử lý bất đồng bộ qua RabbitMQ...</p>
          <div style={{ display: 'flex', gap: 12, marginTop: 24 }}>
            <button style={styles.btn} onClick={() => navigate('/bookings')}>Xem đơn của tôi</button>
            <button style={{ ...styles.btn, background: '#636e72' }} onClick={() => navigate('/movies')}>Tiếp tục mua vé</button>
          </div>
        </div>
      </div>
    </div>
  );

  return (
    <div style={styles.page}>
      <Navbar />
      <div style={styles.center}>
        <div style={styles.card}>
          <h2 style={styles.heading}>🎫 Đặt vé xem phim</h2>
          <div style={styles.movieInfo}>
            <h3 style={{ margin: 0, color: '#e94560' }}>{movie.title}</h3>
            <p style={{ color: '#aaa', margin: '8px 0 0' }}>{movie.genre} · {movie.durationMinutes} phút</p>
          </div>
          <div style={styles.field}>
            <label style={styles.label}>Số ghế muốn đặt:</label>
            <div style={styles.seatRow}>
              <button style={styles.seatBtn} onClick={() => setSeats(Math.max(1, seats - 1))}>−</button>
              <span style={styles.seatNum}>{seats}</span>
              <button style={styles.seatBtn} onClick={() => setSeats(Math.min(movie.availableSeats, seats + 1))}>+</button>
            </div>
          </div>
          <div style={styles.summary}>
            <div style={styles.row}><span>Giá / vé</span><span>{Number(movie.price).toLocaleString('vi-VN')} VND</span></div>
            <div style={styles.row}><span>Số ghế</span><span>{seats}</span></div>
            <div style={{ ...styles.row, fontWeight: 700, fontSize: 18, color: '#fdcb6e', borderTop: '1px solid #333', paddingTop: 12 }}>
              <span>Tổng cộng</span><span>{Number(total).toLocaleString('vi-VN')} VND</span>
            </div>
          </div>
          {error && <div style={styles.error}>{error}</div>}
          <button style={{ ...styles.btn, opacity: status === 'loading' ? 0.7 : 1 }}
            disabled={status === 'loading'} onClick={handleBook}>
            {status === 'loading' ? '⏳ Đang xử lý...' : '💳 Xác nhận đặt vé'}
          </button>
          <button style={{ ...styles.btn, background: 'transparent', color: '#aaa', border: '1px solid #555' }}
            onClick={() => navigate('/movies')}>← Quay lại</button>
        </div>
      </div>
    </div>
  );
}

const styles = {
  page: { minHeight: '100vh', background: '#1a1a2e' },
  center: { display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '40px 16px' },
  card: { background: '#16213e', borderRadius: 12, padding: 36, width: '100%', maxWidth: 460, display: 'flex', flexDirection: 'column', gap: 20 },
  successCard: { background: '#fff', borderRadius: 16, padding: 48, textAlign: 'center', maxWidth: 440 },
  heading: { color: '#fff', margin: 0, fontSize: 22 },
  movieInfo: { background: '#0f3460', borderRadius: 8, padding: 16 },
  field: { display: 'flex', flexDirection: 'column', gap: 10 },
  label: { color: '#aaa', fontSize: 14 },
  seatRow: { display: 'flex', alignItems: 'center', gap: 20 },
  seatBtn: { width: 40, height: 40, borderRadius: 8, background: '#e94560', color: '#fff', border: 'none', fontSize: 20, cursor: 'pointer' },
  seatNum: { fontSize: 28, fontWeight: 700, color: '#fff', minWidth: 40, textAlign: 'center' },
  summary: { display: 'flex', flexDirection: 'column', gap: 10 },
  row: { display: 'flex', justifyContent: 'space-between', color: '#ccc', fontSize: 15 },
  btn: { padding: '14px 0', background: '#e94560', color: '#fff', border: 'none', borderRadius: 8, fontSize: 15, cursor: 'pointer', fontWeight: 600 },
  error: { background: '#fff0f0', color: '#e94560', padding: 12, borderRadius: 8, fontSize: 14 },
};
