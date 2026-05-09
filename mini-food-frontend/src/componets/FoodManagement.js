import React, { useState, useEffect } from 'react';
import { foodApi } from '../api/axiosClient';
import './FoodManagement.css';

function FoodManagement({ isAdmin }) {
  const [foods, setFoods] = useState([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingFood, setEditingFood] = useState(null);
  
  // Chỉnh sửa formData: Bỏ category/image, thêm description theo BE
  const [formData, setFormData] = useState({ name: '', price: '', description: '' });

  const fetchFoods = async () => {
    try {
      // BE @RequestMapping("/foods") nên gọi trực tiếp '/' vì baseURL đã có /api/foods
      const res = await foodApi.get(''); 
      setFoods(res.data);
    } catch (err) {
      console.error("Lỗi lấy danh sách món:", err);
    }
  };

  useEffect(() => { fetchFoods(); }, []);

  const handleDelete = async (id) => {
    if (window.confirm("Bạn có chắc chắn muốn xóa món này?")) {
      try {
        await foodApi.delete(`/${id}`);
        fetchFoods();
      } catch (err) { alert("Xóa thất bại!"); }
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingFood) {
        await foodApi.put(`/${editingFood.id}`, formData);
      } else {
        await foodApi.post('', formData);
      }
      closeModal();
      fetchFoods();
    } catch (err) { alert("Lưu thất bại!"); }
  };

  const openEditModal = (food) => {
    setEditingFood(food);
    // Chỉ lấy đúng các trường BE cần để tránh gửi dư thừa
    setFormData({ 
      name: food.name, 
      price: food.price, 
      description: food.description || '' 
    });
    setIsModalOpen(true);
  };

  const closeModal = () => {
    setIsModalOpen(false);
    setEditingFood(null);
    setFormData({ name: '', price: '', description: '' });
  };

  return (
    <div className="food-container">
      <div className="header-section">
        <h2>{isAdmin ? "Quản Lý Thực Đơn" : "Danh Sách Món Ăn"}</h2>
        {isAdmin && (
          <button className="btn-add" onClick={() => setIsModalOpen(true)}>
            + Thêm Món Mới
          </button>
        )}
      </div>

      {isAdmin ? (
        <table className="food-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Tên món</th>
              <th>Giá</th>
              <th>Mô tả</th> {/* Đổi Loại -> Mô tả */}
              <th>Hành động</th>
            </tr>
          </thead>
          <tbody>
            {foods.map(f => (
              <tr key={f.id}>
                <td>{f.id}</td>
                <td>{f.name}</td>
                <td>{Number(f.price).toLocaleString()}đ</td>
                <td>{f.description}</td>
                <td>
                  <button className="btn-edit" onClick={() => openEditModal(f)}>Sửa</button>
                  <button className="btn-delete" onClick={() => handleDelete(f.id)}>Xóa</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : (
        <div className="food-grid">
          {foods.map(f => (
            <div key={f.id} className="food-card">
              <div className="food-img">🍲</div>
              <h4>{f.name}</h4>
              <p className="description">{f.description}</p>
              <p className="price">{Number(f.price).toLocaleString()}đ</p>
              <button className="btn-cart">Thêm vào giỏ</button>
            </div>
          ))}
        </div>
      )}

      {isModalOpen && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h3>{editingFood ? "Cập nhật món ăn" : "Thêm món mới"}</h3>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Tên món</label>
                <input 
                  value={formData.name} 
                  onChange={e => setFormData({...formData, name: e.target.value})} 
                  required 
                />
              </div>
              <div className="form-group">
                <label>Giá (VNĐ)</label>
                <input 
                  type="number" 
                  value={formData.price} 
                  onChange={e => setFormData({...formData, price: e.target.value})} 
                  required 
                />
              </div>
              <div className="form-group">
                <label>Mô tả món ăn</label>
                <textarea 
                  value={formData.description} 
                  onChange={e => setFormData({...formData, description: e.target.value})} 
                  placeholder="Ví dụ: Cay nồng, nhiều topping..."
                />
              </div>
              <div className="modal-actions">
                <button type="submit" className="btn-save">Lưu</button>
                <button type="button" className="btn-cancel" onClick={closeModal}>Hủy</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default FoodManagement;