import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { movieAPI } from '../services/api';
import { useAuth } from '../context/AuthContext';
import Navbar from '../components/Navbar';

export default function MoviesPage() {
  const [movies, setMovies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selected, setSelected] = useState(null);
  const { user } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (!user) { navigate('/login'); return; }
    movieAPI.getAll().then(r => setMovies(r.data)).finally(() => setLoading(false));
  }, [user, navigate]);

  const genres = { Action: '#e94560', 'Sci-Fi': '#6c5ce7', Drama: '#00b894', Horror: '#2d3436', Comedy: '#fdcb6e' };

  if (loading) return <div style={styles.loading}>Đang tải phim...</div>;

  return (
    <div style={styles.page}>
      <Navbar />
      <div style={styles.content}>
        <h2 style={styles.heading}>🎬 Danh sách phim đang chiếu</h2>
        <div style={styles.grid}>
          {movies.map(movie => (
            <div key={movie.id} style={styles.card}>
              <div style={{ ...styles.genre, background: genres[movie.genre] || '#636e72' }}>
                {movie.genre}
              </div>
              <h3 style={styles.title}>{movie.title}</h3>
              <p style={styles.desc}>{movie.description}</p>
              <div style={styles.info}>
                <span>⏱ {movie.durationMinutes} phút</span>
                <span>💺 {movie.availableSeats} ghế trống</span>
              </div>
              <div style={styles.price}>
                {Number(movie.price).toLocaleString('vi-VN')} VND / vé
              </div>
              <button
                style={{ ...styles.bookBtn, opacity: movie.availableSeats === 0 ? 0.5 : 1 }}
                disabled={movie.availableSeats === 0}
                onClick={() => navigate('/booking', { state: { movie } })}
              >
                {movie.availableSeats === 0 ? 'Hết vé' : '🎫 Đặt vé ngay'}
              </button>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

const styles = {
  page: { minHeight: '100vh', background: '#1a1a2e', color: '#fff' },
  content: { maxWidth: 1100, margin: '0 auto', padding: '24px 16px' },
  heading: { fontSize: 26, marginBottom: 24, color: '#fff' },
  grid: { display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: 24 },
  card: { background: '#16213e', borderRadius: 12, padding: 24, display: 'flex', flexDirection: 'column', gap: 12 },
  genre: { display: 'inline-block', padding: '4px 12px', borderRadius: 20, fontSize: 12, fontWeight: 600, width: 'fit-content' },
  title: { fontSize: 20, fontWeight: 700, margin: 0, color: '#fff' },
  desc: { color: '#aaa', fontSize: 14, margin: 0, lineHeight: 1.5 },
  info: { display: 'flex', gap: 16, fontSize: 13, color: '#74b9ff' },
  price: { fontSize: 18, fontWeight: 700, color: '#fdcb6e' },
  bookBtn: { background: '#e94560', color: '#fff', border: 'none', borderRadius: 8, padding: '12px 0', fontSize: 15, cursor: 'pointer', fontWeight: 600, marginTop: 'auto' },
  loading: { minHeight: '100vh', background: '#1a1a2e', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#fff', fontSize: 20 },
};
