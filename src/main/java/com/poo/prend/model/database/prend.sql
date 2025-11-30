DROP DATABASE IF EXISTS prend;
CREATE DATABASE prend;
USE prend;

-- 1. USUARIOS
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    dni VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    edad INT,
    numero_telefono VARCHAR(20),
    direccion VARCHAR(255),
    correo VARCHAR(100) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 2. EMPRENDIMIENTOS
CREATE TABLE emprendimientos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    rubro VARCHAR(50),
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- 3. CLIENTES
CREATE TABLE clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_emprendimiento INT NOT NULL,
    dni VARCHAR(20),
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100),
    numero_telefono VARCHAR(20),
    direccion VARCHAR(255),
    email VARCHAR(100),
    calificacion INT DEFAULT 5,
    FOREIGN KEY (id_emprendimiento) REFERENCES emprendimientos(id) ON DELETE CASCADE
);

-- 4. PRODUCTOS
CREATE TABLE productos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_emprendimiento INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    precio DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    categoria VARCHAR(50),
    imagen_url VARCHAR(255),
    FOREIGN KEY (id_emprendimiento) REFERENCES emprendimientos(id) ON DELETE CASCADE
);

-- 5. PEDIDOS
CREATE TABLE pedidos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_emprendimiento INT NOT NULL,
    id_cliente INT NOT NULL,
    fecha_pedido DATETIME DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(50) DEFAULT 'PENDIENTE',
    total DECIMAL(10, 2) DEFAULT 0.00,
    observaciones TEXT,
    FOREIGN KEY (id_emprendimiento) REFERENCES emprendimientos(id) ON DELETE CASCADE,
    -- CAMBIO IMPORTANTE: Si borras un cliente, se borran sus pedidos
    FOREIGN KEY (id_cliente) REFERENCES clientes(id) ON DELETE CASCADE 
);

-- 6. ITEMS PEDIDO
CREATE TABLE items_pedido (
    id INT AUTO_INCREMENT PRIMARY KEY,
    pedido_id INT NOT NULL,
    producto_id INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
    -- CAMBIO IMPORTANTE: Si borras un producto, se borra de los items de pedido
    FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE 
);

-- 7. VENTAS
CREATE TABLE ventas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    pedido_id INT NOT NULL UNIQUE,
    metodo_pago VARCHAR(50) NOT NULL,
    fecha_venta DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE
);