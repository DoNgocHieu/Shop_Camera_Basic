-- Insert categories
INSERT INTO categories (name, description, active) VALUES 
('Máy ảnh DSLR', 'Máy ảnh chuyên dùng cho chụp ảnh phong cảnh, chân dung, sự kiện', true),
('Máy ảnh Compact', 'Máy ảnh nhỏ gọn, tiện lợi cho du lịch và sử dụng hàng ngày', true),
('Máy ảnh Mirrorless', 'Máy ảnh không gương lật, nhỏ gọn, chất lượng cao', true),
('Camera hành trình', 'Máy ảnh hành trình, gắn xe, gắn mũ bảo hiểm', true),
('Máy ảnh Instax', 'Máy ảnh lấy liền, chụp và in ảnh ngay', true);

-- Insert sample users (password is encoded for 'password123')
INSERT INTO users (username, email, password, full_name, phone_number, address, role, active) VALUES 
('admin', 'admin@mayanh.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Quản trị viên', '0123456789', 'Hà Nội, Việt Nam', 'ADMIN', true),
('user1', 'user1@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Nguyễn Văn A', '0987654321', 'TP.HCM, Việt Nam', 'USER', true),
('user2', 'user2@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Trần Thị B', '0912345678', 'Đà Nẵng, Việt Nam', 'USER', true);
-- Test
-- admin / password
-- user1 / password
-- user2 / password
-- Insert sample cameras
INSERT INTO cameras (name, brand, price, description, image_url, stock_quantity, active, category_id) VALUES 
-- Máy ảnh DSLR (category_id = 5)
('Canon EOS 5D Mark IV', 'Canon', 55000000, 'Máy ảnh DSLR chuyên nghiệp, cảm biến full-frame, quay phim 4K', 'https://images.unsplash.com/photo-1519125323398-675f0ddb6308?w=400&h=400&fit=crop', 5, true, 5),
('Nikon D850', 'Nikon', 65000000, 'Máy ảnh DSLR cao cấp, độ phân giải 45.7MP, tốc độ chụp nhanh', 'https://images.unsplash.com/photo-1464983953574-0892a716854b?w=400&h=400&fit=crop', 8, true, 5),

-- Máy ảnh Compact (category_id = 6)
('Sony RX100 VII', 'Sony', 25000000, 'Máy ảnh compact nhỏ gọn, cảm biến 1 inch, zoom quang 8x', 'https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=400&h=400&fit=crop', 10, true, 6),
('Canon PowerShot G7 X Mark III', 'Canon', 18000000, 'Máy ảnh compact quay vlog, cảm biến 20MP, màn hình lật', 'https://images.unsplash.com/photo-1519125323398-675f0ddb6308?w=400&h=400&fit=crop', 6, true, 6),

-- Máy ảnh Mirrorless (category_id = 7)
('Fujifilm X-T4', 'Fujifilm', 32000000, 'Máy ảnh mirrorless chống rung 5 trục, quay 4K 60fps', 'https://images.unsplash.com/photo-1519125323398-675f0ddb6308?w=400&h=400&fit=crop', 12, true, 7),
('Sony Alpha a6400', 'Sony', 21000000, 'Máy ảnh mirrorless lấy nét nhanh, màn hình cảm ứng lật', 'https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=400&h=400&fit=crop', 8, true, 7),

-- Camera hành trình (category_id = 8)
('GoPro HERO12 Black', 'GoPro', 12000000, 'Camera hành trình chống nước, quay 5.3K, chống rung HyperSmooth', 'https://images.unsplash.com/photo-1519125323398-675f0ddb6308?w=400&h=400&fit=crop', 20, true, 8),
('DJI Osmo Action 4', 'DJI', 9500000, 'Camera hành trình quay 4K, màn hình kép, chống rung RockSteady', 'https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=400&h=400&fit=crop', 15, true, 8),

-- Máy ảnh Instax (category_id = 9)
('Fujifilm Instax Mini 12', 'Fujifilm', 2000000, 'Máy ảnh chụp lấy liền, thiết kế trẻ trung, nhiều màu sắc', 'https://images.unsplash.com/photo-1519125323398-675f0ddb6308?w=400&h=400&fit=crop', 10, true, 9),
('Polaroid Now+', 'Polaroid', 3500000, 'Máy ảnh Instax kết nối Bluetooth, nhiều chế độ chụp sáng tạo', 'https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=400&h=400&fit=crop', 8, true, 9);
