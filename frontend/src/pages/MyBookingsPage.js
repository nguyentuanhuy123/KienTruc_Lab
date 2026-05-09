import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { bookingAPI } from '../services/api';
import { useAuth } from '../context/AuthContext';
import Navbar from '../components/Navbar';

const STATUS_COLORS = { PENDING: '#e67e22', PAID: '#27ae60', FAILED: '#e74c3c' };
const STATUS_LABEL = { PENDING: '⏳ Đang xử lý', PAID: '✅ Thành công', FAILED: '❌ Thất bại' };

export default function MyBookingsPage() {
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const { user } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (!user) { navigate('/login'); return; }
    bookingAPI.getByUser(user.userId)
      .then(r => setBookings(r.data))
      .finally(() => setLoading(false));
  }, [user, navigate]);

  const refresh = () => {
    setLoading(true);
    bookingAPI.getByUser(user.userId).then(r => setBookings(r.data)).finally(() => setLoading(false));
  };

  return (
    <div style={styles.page}>
      <Navbar />
      <div style={styles.content}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24 }}>
          <h2 style={styles.heading}>🎫 Đơn vé của tôi</h2>
          <button style={styles.refreshBtn} onClick={refresh}>🔄 Làm mới</button>
        </div>
        {loading ? (
          <p style={{ color: '#aaa', textAlign: 'center' }}>Đang tải...</p>
        ) : bookings.length === 0 ? (
          <div style={styles.empty}>
            <p>Chưa có đơn đặt vé nào.</p>
            <button style={styles.btn} onClick={() => navigate('/movies')}>Đặt vé ngay</button>
          </div>
        ) : (
          <div style={styles.list}>
            {bookings.map(b => (
              <div key={b.id} style={styles.item}>
                <div style={styles.itemLeft}>
                  <div style={styles.bookingId}>Booking #{b.id}</div>
                  <div style={styles.movieName}>{b.movieTitle}</div>
                  <div style={styles.detail}>{b.seats} ghế · {Number(b.totalPrice).toLocaleString('vi-VN')} VND</div>
                  <div style={styles.date}>{new Date(b.createdAt).toLocaleString('vi-VN')}</div>
                </div>
                <div style={{ ...styles.badge, background: STATUS_COLORS[b.status] || '#636e72' }}>
                  {STATUS_LABEL[b.status] || b.status}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

const styles = {
  page: { minHeight: '100vh', background: '#1a1a2e', color: '#fff' },
  content: { maxWidth: 700, margin: '0 auto', padding: '24px 16px' },
  heading: { color: '#fff', margin: 0 },
  list: { display: 'flex', flexDirection: 'column', gap: 16 },
  item: { background: '#16213e', borderRadius: 12, padding: 20, display: 'flex', justifyContent: 'space-between', alignItems: 'center' },
  itemLeft: { display: 'flex', flexDirection: 'column', gap: 4 },
  bookingId: { color: '#74b9ff', fontSize: 13, fontWeight: 600 },
  movieName: { fontSize: 18, fontWeight: 700 },
  detail: { color: '#fdcb6e', fontSize: 14 },
  date: { color: '#888', fontSize: 12 },
  badge: { padding: '8px 16px', borderRadius: 20, fontSize: 13, fontWeight: 600, whiteSpace: 'nowrap' },
  refreshBtn: { background: '#0f3460', color: '#74b9ff', border: '1px solid #74b9ff', borderRadius: 8, padding: '8px 16px', cursor: 'pointer' },
  empty: { textAlign: 'center', color: '#aaa', padding: 48 },
  btn: { background: '#e94560', color: '#fff', border: 'none', borderRadius: 8, padding: '12px 24px', cursor: 'pointer', fontWeight: 600, fontSize: 15 },
};
