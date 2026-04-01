-- ============================================================
-- BenNghe Bookstore - SQL Server Init Script
-- ============================================================

USE master;
GO

IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'BenNgheBook')
BEGIN
    CREATE DATABASE BenNgheBook COLLATE Vietnamese_CI_AS;
END
GO

USE BenNgheBook;
GO

-- ============================================================
-- DROP tables if exist (for re-run)
-- ============================================================
IF OBJECT_ID('haravan_sync_logs', 'U') IS NOT NULL DROP TABLE haravan_sync_logs;
IF OBJECT_ID('sales_records', 'U') IS NOT NULL DROP TABLE sales_records;
IF OBJECT_ID('inventory_receipt_items', 'U') IS NOT NULL DROP TABLE inventory_receipt_items;
IF OBJECT_ID('inventory_receipts', 'U') IS NOT NULL DROP TABLE inventory_receipts;
IF OBJECT_ID('book_authors', 'U') IS NOT NULL DROP TABLE book_authors;
IF OBJECT_ID('books', 'U') IS NOT NULL DROP TABLE books;
IF OBJECT_ID('authors', 'U') IS NOT NULL DROP TABLE authors;
IF OBJECT_ID('publishers', 'U') IS NOT NULL DROP TABLE publishers;
IF OBJECT_ID('users', 'U') IS NOT NULL DROP TABLE users;
IF OBJECT_ID('role_permissions', 'U') IS NOT NULL DROP TABLE role_permissions;
IF OBJECT_ID('permissions', 'U') IS NOT NULL DROP TABLE permissions;
IF OBJECT_ID('roles', 'U') IS NOT NULL DROP TABLE roles;
GO

-- ============================================================
-- ROLES & PERMISSIONS
-- ============================================================
CREATE TABLE roles (
    id    INT IDENTITY(1,1) PRIMARY KEY,
    name  NVARCHAR(50)  NOT NULL UNIQUE,
    description NVARCHAR(255)
);

CREATE TABLE permissions (
    id    INT IDENTITY(1,1) PRIMARY KEY,
    name  NVARCHAR(100) NOT NULL UNIQUE,
    description NVARCHAR(255)
);

CREATE TABLE role_permissions (
    role_id       INT NOT NULL,
    permission_id INT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_rp_role FOREIGN KEY (role_id)       REFERENCES roles(id),
    CONSTRAINT fk_rp_perm FOREIGN KEY (permission_id) REFERENCES permissions(id)
);

-- ============================================================
-- USERS
-- ============================================================
CREATE TABLE users (
    id            INT IDENTITY(1,1) PRIMARY KEY,
    username      NVARCHAR(50)  NOT NULL UNIQUE,
    password_hash NVARCHAR(255) NOT NULL,   -- BCrypt
    full_name     NVARCHAR(100),
    email         NVARCHAR(100),
    role_id       INT NOT NULL,
    is_active     BIT DEFAULT 1,
    created_at    DATETIME2 DEFAULT SYSDATETIME(),
    updated_at    DATETIME2 DEFAULT SYSDATETIME(),
    CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- ============================================================
-- PUBLISHERS & AUTHORS
-- ============================================================
CREATE TABLE publishers (
    id         INT IDENTITY(1,1) PRIMARY KEY,
    name       NVARCHAR(255) NOT NULL UNIQUE,
    created_at DATETIME2 DEFAULT SYSDATETIME()
);

CREATE TABLE authors (
    id         INT IDENTITY(1,1) PRIMARY KEY,
    name       NVARCHAR(255) NOT NULL,
    created_at DATETIME2 DEFAULT SYSDATETIME()
);

-- ============================================================
-- BOOKS (SKU)
-- ============================================================
CREATE TABLE books (
    id                INT IDENTITY(1,1) PRIMARY KEY,
    sku               NVARCHAR(50)  NOT NULL UNIQUE,
    isbn              NVARCHAR(20),
    title             NVARCHAR(500) NOT NULL,
    publisher_id      INT,
    publication_year  INT,
    description       NVARCHAR(MAX),
    cover_image_url   NVARCHAR(500),
    warehouse         NVARCHAR(100),
    shelf             NVARCHAR(100),
    import_price      DECIMAL(18,2),
    sale_price        DECIMAL(18,2),
    cover_price       DECIMAL(18,2),     -- Giá bìa
    page_count        INT,
    weight_gram       INT,               -- Trọng lượng (gr)
    size              NVARCHAR(50),      -- Kích thước, vd: 14.5x20.5
    target_audience   NVARCHAR(255),
    distributor       NVARCHAR(255),     -- Nhà phát hành
    translator        NVARCHAR(255),     -- Dịch giả
    is_active         BIT DEFAULT 1,
    created_at        DATETIME2 DEFAULT SYSDATETIME(),
    updated_at        DATETIME2 DEFAULT SYSDATETIME(),
    created_by        INT,
    CONSTRAINT fk_book_publisher FOREIGN KEY (publisher_id) REFERENCES publishers(id),
    CONSTRAINT fk_book_creator   FOREIGN KEY (created_by)   REFERENCES users(id)
);

CREATE TABLE book_authors (
    book_id   INT NOT NULL,
    author_id INT NOT NULL,
    PRIMARY KEY (book_id, author_id),
    CONSTRAINT fk_ba_book   FOREIGN KEY (book_id)   REFERENCES books(id),
    CONSTRAINT fk_ba_author FOREIGN KEY (author_id) REFERENCES authors(id)
);

-- ============================================================
-- INVENTORY RECEIPTS (Nhập kho)
-- ============================================================
CREATE TABLE inventory_receipts (
    id            INT IDENTITY(1,1) PRIMARY KEY,
    receipt_code  NVARCHAR(50)  UNIQUE,   -- mã phiếu nhập
    receipt_date  DATETIME2 DEFAULT SYSDATETIME(),
    imported_by   INT NOT NULL,
    notes         NVARCHAR(500),
    import_source NVARCHAR(20) DEFAULT 'MANUAL',  -- MANUAL | EXCEL
    created_at    DATETIME2 DEFAULT SYSDATETIME(),
    CONSTRAINT fk_ir_user FOREIGN KEY (imported_by) REFERENCES users(id)
);

CREATE TABLE inventory_receipt_items (
    id           INT IDENTITY(1,1) PRIMARY KEY,
    receipt_id   INT NOT NULL,
    book_id      INT NOT NULL,
    quantity     INT NOT NULL,
    import_price DECIMAL(18,2),
    CONSTRAINT fk_iri_receipt FOREIGN KEY (receipt_id) REFERENCES inventory_receipts(id),
    CONSTRAINT fk_iri_book    FOREIGN KEY (book_id)    REFERENCES books(id)
);

-- ============================================================
-- SALES RECORDS (từ Haravan)
-- ============================================================
CREATE TABLE sales_records (
    id               INT IDENTITY(1,1) PRIMARY KEY,
    book_id          INT NOT NULL,
    quantity_sold    INT NOT NULL,
    sale_date        DATE NOT NULL,
    haravan_order_id NVARCHAR(100),
    unit_price       DECIMAL(18,2),
    synced_at        DATETIME2 DEFAULT SYSDATETIME(),
    CONSTRAINT fk_sr_book FOREIGN KEY (book_id) REFERENCES books(id)
);

-- ============================================================
-- HARAVAN SYNC LOGS
-- ============================================================
CREATE TABLE haravan_sync_logs (
    id                 INT IDENTITY(1,1) PRIMARY KEY,
    sync_date          DATETIME2 DEFAULT SYSDATETIME(),
    file_name          NVARCHAR(255),
    synced_by          INT,
    records_processed  INT DEFAULT 0,
    records_succeeded  INT DEFAULT 0,
    records_failed     INT DEFAULT 0,
    status             NVARCHAR(20),     -- SUCCESS | PARTIAL | FAILED
    error_details      NVARCHAR(MAX),
    CONSTRAINT fk_hsl_user FOREIGN KEY (synced_by) REFERENCES users(id)
);
GO

-- ============================================================
-- INDEXES
-- ============================================================
CREATE INDEX idx_books_sku       ON books(sku);
CREATE INDEX idx_books_title     ON books(title);
CREATE INDEX idx_books_publisher ON books(publisher_id);
CREATE INDEX idx_ir_date         ON inventory_receipts(receipt_date);
CREATE INDEX idx_sr_date         ON sales_records(sale_date);
CREATE INDEX idx_sr_book         ON sales_records(book_id);
GO

-- ============================================================
-- SEED DATA - Roles
-- ============================================================
INSERT INTO roles (name, description) VALUES
    (N'ADMIN',   N'Quản trị hệ thống - toàn quyền'),
    (N'MANAGER', N'Quản lý - xem và chỉnh sửa dữ liệu'),
    (N'STAFF',   N'Nhân viên - nhập kho và xem báo cáo cơ bản');
GO

-- ============================================================
-- SEED DATA - Permissions
-- ============================================================
INSERT INTO permissions (name, description) VALUES
    (N'USER_CREATE',      N'Tạo tài khoản người dùng'),
    (N'USER_READ',        N'Xem danh sách người dùng'),
    (N'USER_UPDATE',      N'Cập nhật người dùng'),
    (N'USER_DELETE',      N'Xoá người dùng'),
    (N'BOOK_CREATE',      N'Thêm sách mới (SKU)'),
    (N'BOOK_READ',        N'Xem danh sách sách'),
    (N'BOOK_UPDATE',      N'Cập nhật thông tin sách'),
    (N'BOOK_DELETE',      N'Xoá sách'),
    (N'INVENTORY_CREATE', N'Nhập kho'),
    (N'INVENTORY_READ',   N'Xem lịch sử nhập kho'),
    (N'SALES_READ',       N'Xem dữ liệu bán hàng'),
    (N'HARAVAN_IMPORT',   N'Import file Haravan'),
    (N'HARAVAN_EXPORT',   N'Xuất file Excel Haravan'),
    (N'DASHBOARD_READ',   N'Xem dashboard');
GO

-- ============================================================
-- SEED DATA - Role Permissions
-- ============================================================
-- ADMIN: all permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT 1, id FROM permissions;

-- MANAGER: all except user management
INSERT INTO role_permissions (role_id, permission_id)
SELECT 2, id FROM permissions
WHERE name NOT IN (N'USER_CREATE', N'USER_UPDATE', N'USER_DELETE');

-- STAFF: limited permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT 3, id FROM permissions
WHERE name IN (N'BOOK_READ', N'INVENTORY_CREATE', N'INVENTORY_READ', N'DASHBOARD_READ');
GO

-- ============================================================
-- SEED DATA - Users
-- NOTE: Passwords are seeded by Spring Boot DataInitializer on startup.
--       Placeholder hash below = BCrypt("Admin@123")
-- ============================================================
INSERT INTO users (username, password_hash, full_name, email, role_id) VALUES
    (N'admin',    N'$2a$10$placeholder_replace_on_startup', N'Quản trị viên',  N'admin@bennghe.vn',   1),
    (N'manager1', N'$2a$10$placeholder_replace_on_startup', N'Nguyễn Văn An',  N'an@bennghe.vn',      2),
    (N'manager2', N'$2a$10$placeholder_replace_on_startup', N'Trần Thị Bình',  N'binh@bennghe.vn',    2),
    (N'staff1',   N'$2a$10$placeholder_replace_on_startup', N'Lê Văn Cường',   N'cuong@bennghe.vn',   3),
    (N'staff2',   N'$2a$10$placeholder_replace_on_startup', N'Phạm Thị Dung',  N'dung@bennghe.vn',    3);
GO

-- ============================================================
-- SEED DATA - Publishers (Nhà xuất bản)
-- ============================================================
INSERT INTO publishers (name) VALUES
    (N'NXB Kim Đồng'),
    (N'NXB Trẻ'),
    (N'NXB Tổng Hợp TP.HCM'),
    (N'NXB Hội Nhà Văn'),
    (N'NXB Văn Học'),
    (N'NXB Phụ Nữ'),
    (N'NXB Lao Động'),
    (N'NXB Thanh Niên'),
    (N'NXB Giáo Dục Việt Nam'),
    (N'NXB Dân Trí');
GO

-- ============================================================
-- SEED DATA - Authors (Tác giả)
-- ============================================================
INSERT INTO authors (name) VALUES
    (N'Tô Hoài'),
    (N'Nguyễn Nhật Ánh'),
    (N'Nam Cao'),
    (N'Nguyễn Du'),
    (N'Xuân Quỳnh'),
    (N'Trang Neko'),
    (N'Phát Dương'),
    (N'Minh Nhựt'),
    (N'Ts. Matt Agnew'),
    (N'Christoph Hein'),
    (N'Rotraut Susanne Berner'),
    (N'X.Lan'),
    (N'Paulo Coelho'),
    (N'Dale Carnegie'),
    (N'Napoleon Hill'),
    (N'Haruki Murakami'),
    (N'Antoine de Saint-Exupéry'),
    (N'George Orwell'),
    (N'Fyodor Dostoevsky'),
    (N'Victor Hugo');
GO

-- ============================================================
-- SEED DATA - Books
-- ============================================================
INSERT INTO books (sku, isbn, title, publisher_id, publication_year, cover_price, import_price, sale_price, page_count, weight_gram, size, target_audience, distributor, warehouse, shelf, created_by) VALUES
    (N'5241800010001', N'8935352603000', N'Học sinh chúng mình 2000 hồi ấy',         1, 2022,  85000,  60000,  79000, 200, 350, N'20x20',       N'Thanh niên - Trên 18 tuổi',     N'NXB Kim Đồng',             N'KHO-A', N'KE-01', 1),
    (N'5252300010136', N'9786042393683', N'"Ây Ai" có gì sai? Những điều cơ bản về đạo đức AI', 1, 2023, 120000, 84000, 110000, 180, 400, N'21x25', N'Nhi đồng, thiếu niên (6-15)', N'NXB Kim Đồng', N'KHO-A', N'KE-02', 1),
    (N'5251500010093', N'9786042258777', N'100 chiếc ghe',                            1, 2021,  65000,  45000,  60000, 160, 280, N'14.5x20.5',   N'Nhi đồng (6-11 tuổi)',          N'NXB Kim Đồng',             N'KHO-A', N'KE-01', 1),
    (N'5201100010071', N'8935244849356', N'100 năm ngày sinh nhà văn Tô Hoài - Tự truyện', 2, 2020, 95000, 66000, 88000, 320, 520, N'13x20.5', N'Tuổi mới lớn, thanh niên', N'NXB Trẻ', N'KHO-A', N'KE-03', 1),
    (N'6201500010028', N'8935244833737', N'12 cung hành động',                        2, 2021,  72000,  50000,  68000, 240, 380, N'16x22',       N'Tuổi mới lớn (15-18 tuổi)',     N'NXB Trẻ',                  N'KHO-A', N'KE-03', 1),
    (N'6231800010021', N'8935244843415', N'199 mấy hồi ấy làm gì?',                  2, 2022,  89000,  62000,  82000, 210, 360, N'15x21',       N'Thanh niên - Trên 18 tuổi',     N'NXB Trẻ',                  N'KHO-B', N'KE-01', 1),
    (N'5242400010105', N'8935352618493', N'20 điều quan trọng nhất - Nói với con về những điều quý giá trong đời', 1, 2023, 150000, 105000, 139000, 64, 500, N'16x24', N'Thiếu niên, thanh niên', N'NXB Kim Đồng', N'KHO-B', N'KE-02', 1),
    (N'5300100010011', N'9786041187800', N'Dế mèn phiêu lưu ký',                     1, 2019,  45000,  31000,  42000, 192, 290, N'13x19',       N'Thiếu nhi',                     N'NXB Kim Đồng',             N'KHO-B', N'KE-01', 1),
    (N'5300200010022', N'9786041102293', N'Cho tôi xin một vé đi tuổi thơ',          2, 2020,  75000,  52000,  70000, 228, 330, N'14x20',       N'Thiếu niên, thanh niên',        N'NXB Trẻ',                  N'KHO-B', N'KE-03', 1),
    (N'5300300010033', N'9786041102057', N'Tôi thấy hoa vàng trên cỏ xanh',          2, 2020,  80000,  56000,  75000, 268, 370, N'14x20',       N'Thiếu niên, thanh niên',        N'NXB Trẻ',                  N'KHO-C', N'KE-01', 1),
    (N'5300400010044', N'9786041143487', N'Mắt biếc',                                 2, 2019,  68000,  47000,  64000, 240, 340, N'14x20',       N'Thiếu niên, thanh niên',        N'NXB Trẻ',                  N'KHO-C', N'KE-01', 1),
    (N'5300500010055', N'9786041199880', N'Đắc nhân tâm',                             3, 2018,  99000,  69000,  92000, 320, 480, N'14x20.5',     N'Thanh niên - Trên 18 tuổi',     N'NXB Tổng Hợp TP.HCM',      N'KHO-C', N'KE-02', 1),
    (N'5300600010066', N'9786041197800', N'Nghĩ giàu làm giàu',                       3, 2018, 109000,  76000, 100000, 380, 560, N'14x20.5',     N'Thanh niên - Trên 18 tuổi',     N'NXB Tổng Hợp TP.HCM',      N'KHO-C', N'KE-02', 1),
    (N'5300700010077', N'9786041212320', N'Nhà giả kim',                               3, 2020,  79000,  55000,  74000, 228, 310, N'13x20',       N'Thanh niên - Trên 18 tuổi',     N'NXB Tổng Hợp TP.HCM',      N'KHO-C', N'KE-03', 1),
    (N'5300800010088', N'9786041230576', N'Rừng Na Uy',                                3, 2021, 120000,  84000, 112000, 364, 520, N'14x20',       N'Thanh niên - Trên 18 tuổi',     N'NXB Tổng Hợp TP.HCM',      N'KHO-D', N'KE-01', 1),
    (N'5300900010099', N'9786041177499', N'Hoàng tử bé',                               5, 2022,  55000,  38000,  52000, 100, 180, N'13x19',       N'Thiếu nhi, thiếu niên',         N'NXB Văn Học',              N'KHO-D', N'KE-01', 1),
    (N'5301000010100', N'9786041188869', N'Trại súc vật',                               5, 2021,  65000,  45000,  60000, 132, 220, N'13x19',       N'Thanh niên - Trên 18 tuổi',     N'NXB Văn Học',              N'KHO-D', N'KE-02', 1),
    (N'5301100010111', N'9786041193795', N'Tội ác và trừng phạt',                      5, 2019, 195000, 136000, 180000, 680, 980, N'14x20.5',     N'Thanh niên - Trên 18 tuổi',     N'NXB Văn Học',              N'KHO-D', N'KE-02', 1),
    (N'5301200010122', N'9786041205741', N'Những người khốn khổ - Tập 1',              5, 2020, 135000,  94000, 125000, 520, 760, N'14x20.5',     N'Thanh niên - Trên 18 tuổi',     N'NXB Văn Học',              N'KHO-D', N'KE-03', 1),
    (N'5301300010133', N'9786041182393', N'Chí Phèo và các truyện ngắn khác',          4, 2020,  72000,  50000,  67000, 256, 370, N'13x19',       N'Thanh niên - Trên 18 tuổi',     N'NXB Hội Nhà Văn',          N'KHO-E', N'KE-01', 1),
    (N'5301400010144', N'9786041163737', N'Truyện Kiều',                                4, 2019,  85000,  59000,  79000, 298, 420, N'14x20',       N'Thanh niên - Trên 18 tuổi',     N'NXB Hội Nhà Văn',          N'KHO-E', N'KE-01', 1),
    (N'5301500010155', N'9786041195812', N'Thơ Xuân Quỳnh - Tuyển tập',                4, 2021,  78000,  54000,  72000, 220, 300, N'13x19',       N'Thanh niên - Trên 18 tuổi',     N'NXB Hội Nhà Văn',          N'KHO-E', N'KE-02', 1),
    (N'5301600010166', N'9786041201218', N'Muôn kiếp nhân sinh',                        7, 2021, 145000, 101000, 135000, 398, 580, N'14x20.5',     N'Thanh niên - Trên 18 tuổi',     N'NXB Lao Động',             N'KHO-E', N'KE-02', 1),
    (N'5301700010177', N'9786041190177', N'Hành trình về phương Đông',                  7, 2020, 139000,  97000, 129000, 430, 620, N'14x20.5',     N'Thanh niên - Trên 18 tuổi',     N'NXB Lao Động',             N'KHO-E', N'KE-03', 1),
    (N'5301800010188', N'9786041184205', N'Tuổi thơ dữ dội - Tập 1',                   8, 2020,  89000,  62000,  83000, 320, 480, N'14x20',       N'Thiếu niên',                    N'NXB Thanh Niên',           N'KHO-F', N'KE-01', 1),
    (N'5301900010199', N'9786041167834', N'Không gia đình',                              9, 2019,  95000,  66000,  88000, 420, 610, N'14x20',       N'Thiếu niên',                    N'NXB Giáo Dục Việt Nam',    N'KHO-F', N'KE-01', 1),
    (N'5302000010200', N'9786041198883', N'Tâm lý học đám đông',                        3, 2021, 112000,  78000, 104000, 298, 430, N'14x20',       N'Thanh niên - Trên 18 tuổi',     N'NXB Tổng Hợp TP.HCM',      N'KHO-F', N'KE-02', 1),
    (N'5302100010211', N'9786041204118', N'Sapiens: Lược sử loài người',                3, 2022, 189000, 132000, 175000, 576, 820, N'14.5x20.5',   N'Thanh niên - Trên 18 tuổi',     N'NXB Tổng Hợp TP.HCM',      N'KHO-F', N'KE-02', 1),
    (N'5302200010222', N'9786041208124', N'Đừng bao giờ đi ăn một mình',               10, 2021, 125000,  87000, 115000, 360, 520, N'14x20.5',     N'Thanh niên - Trên 18 tuổi',     N'NXB Dân Trí',              N'KHO-F', N'KE-03', 1),
    (N'5302300010233', N'9786041206410', N'Kỹ năng tư duy phản biện',                  10, 2022,  98000,  68000,  91000, 278, 400, N'14x20',       N'Thanh niên - Trên 18 tuổi',     N'NXB Dân Trí',              N'KHO-G', N'KE-01', 1);
GO

-- ============================================================
-- SEED DATA - Book Authors
-- ============================================================
INSERT INTO book_authors (book_id, author_id) VALUES
    (1,  6),   -- Học sinh chúng mình / Trang Neko
    (1,  12),  -- Học sinh chúng mình / X.Lan
    (2,  9),   -- Ây Ai / Ts. Matt Agnew
    (3,  7),   -- 100 chiếc ghe / Phát Dương
    (4,  1),   -- Tô Hoài / Tô Hoài
    (5,  8),   -- 12 cung hành động / Minh Nhựt
    (7,  10),  -- 20 điều / Christoph Hein
    (7,  11),  -- 20 điều / Rotraut Susanne Berner
    (8,  1),   -- Dế mèn / Tô Hoài
    (9,  2),   -- Cho tôi xin / Nguyễn Nhật Ánh
    (10, 2),   -- Tôi thấy hoa vàng / Nguyễn Nhật Ánh
    (11, 2),   -- Mắt biếc / Nguyễn Nhật Ánh
    (12, 14),  -- Đắc nhân tâm / Dale Carnegie
    (13, 15),  -- Nghĩ giàu / Napoleon Hill
    (14, 13),  -- Nhà giả kim / Paulo Coelho
    (15, 16),  -- Rừng Na Uy / Haruki Murakami
    (16, 17),  -- Hoàng tử bé / Antoine de Saint-Exupéry
    (17, 18),  -- Trại súc vật / George Orwell
    (18, 19),  -- Tội ác / Fyodor Dostoevsky
    (19, 20),  -- Những người khốn khổ / Victor Hugo
    (20, 3),   -- Chí Phèo / Nam Cao
    (21, 4),   -- Truyện Kiều / Nguyễn Du
    (22, 5),   -- Xuân Quỳnh / Xuân Quỳnh
    (25, 2),   -- Tuổi thơ dữ dội / Nguyễn Nhật Ánh (placeholder)
    (28, 16);  -- Tâm lý / Murakami (placeholder)
GO

-- ============================================================
-- SEED DATA - Inventory Receipts (Nhập kho)
-- ============================================================
DECLARE @today DATETIME2 = SYSDATETIME();

INSERT INTO inventory_receipts (receipt_code, receipt_date, imported_by, notes, import_source) VALUES
    (N'PN-2024-001', DATEADD(DAY, -90, @today), 2, N'Nhập kho đợt đầu năm 2024',          N'MANUAL'),
    (N'PN-2024-002', DATEADD(DAY, -60, @today), 2, N'Nhập bổ sung tháng 3',                N'MANUAL'),
    (N'PN-2024-003', DATEADD(DAY, -45, @today), 4, N'Import từ file Excel nhà cung cấp',   N'EXCEL'),
    (N'PN-2024-004', DATEADD(DAY, -30, @today), 2, N'Nhập kho tháng 4',                    N'MANUAL'),
    (N'PN-2024-005', DATEADD(DAY, -15, @today), 4, N'Nhập bổ sung sách bán chạy',          N'MANUAL');
GO

-- Receipt 1: nhập nhiều sách ban đầu
INSERT INTO inventory_receipt_items (receipt_id, book_id, quantity, import_price) VALUES
    (1,  1, 50, 60000), (1,  2, 30, 84000), (1,  3, 40, 45000),
    (1,  4, 25, 66000), (1,  5, 30, 50000), (1,  6, 35, 62000),
    (1,  8, 60, 31000), (1,  9, 50, 52000), (1, 10, 45, 56000),
    (1, 11, 40, 47000), (1, 12, 80, 69000), (1, 13, 60, 76000),
    (1, 14, 70, 55000), (1, 15, 50, 84000), (1, 16, 90, 38000);

-- Receipt 2
INSERT INTO inventory_receipt_items (receipt_id, book_id, quantity, import_price) VALUES
    (2, 17, 40, 45000), (2, 18, 25, 136000), (2, 19, 30, 94000),
    (2, 20, 45, 50000), (2, 21, 35, 59000),  (2, 22, 30, 54000),
    (2, 23, 40, 101000),(2, 24, 35, 97000),  (2, 25, 50, 62000);

-- Receipt 3
INSERT INTO inventory_receipt_items (receipt_id, book_id, quantity, import_price) VALUES
    (3, 26, 40, 66000), (3, 27, 30, 66000), (3, 28, 50, 78000),
    (3, 29, 35, 132000),(3, 30, 40, 87000);

-- Receipt 4
INSERT INTO inventory_receipt_items (receipt_id, book_id, quantity, import_price) VALUES
    (4,  1, 20, 60000), (4,  9, 25, 52000), (4, 12, 30, 69000),
    (4, 14, 20, 55000), (4, 29, 15, 132000);

-- Receipt 5
INSERT INTO inventory_receipt_items (receipt_id, book_id, quantity, import_price) VALUES
    (5,  8, 30, 31000), (5, 10, 20, 56000), (5, 11, 25, 47000),
    (5, 16, 40, 38000), (5, 22, 20, 54000);
GO

-- ============================================================
-- SEED DATA - Sales Records (dữ liệu bán hàng giả lập)
-- ============================================================
DECLARE @base DATE = CAST(DATEADD(DAY, -90, SYSDATETIME()) AS DATE);

-- Simulate daily sales for 90 days, popular books sell more
;WITH days AS (
    SELECT TOP 90 ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) - 1 AS d
    FROM sys.all_objects
)
INSERT INTO sales_records (book_id, quantity_sold, sale_date, haravan_order_id, unit_price)
SELECT
    b.book_id,
    b.qty,
    DATEADD(DAY, d.d, @base),
    N'HRV-' + CAST(ABS(CHECKSUM(NEWID())) % 900000 + 100000 AS NVARCHAR(10)),
    b.price
FROM days d
CROSS JOIN (VALUES
    (8,  3, 42000),   -- Dế mèn - bán chạy
    (9,  2, 70000),   -- Cho tôi xin
    (12, 3, 92000),   -- Đắc nhân tâm - bán chạy
    (14, 2, 74000),   -- Nhà giả kim
    (16, 2, 52000),   -- Hoàng tử bé
    (10, 1, 75000),   -- Tôi thấy hoa vàng
    (11, 1, 64000),   -- Mắt biếc
    (29, 1, 175000),  -- Sapiens
    (1,  1, 79000),   -- Học sinh chúng mình
    (23, 1, 135000)   -- Muôn kiếp nhân sinh
) b(book_id, qty, price)
WHERE d.d % 2 = 0 OR b.book_id IN (8, 12);  -- top books sell every day
GO

PRINT 'Database BenNgheBook initialized successfully!';
PRINT 'Default accounts (passwords set by Spring Boot on first startup):';
PRINT '  admin    / Admin@123   (ADMIN)';
PRINT '  manager1 / Manager@123 (MANAGER)';
PRINT '  manager2 / Manager@123 (MANAGER)';
PRINT '  staff1   / Staff@123   (STAFF)';
PRINT '  staff2   / Staff@123   (STAFF)';
GO
