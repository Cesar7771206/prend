CREATE DATABASE erp_microemprendimientos;

USE erp_microemprendimientos;

CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    dni VARCHAR(8), -- Se modifico la bd para que sea solo 8 digitos el dni
    nombre VARCHAR(100),
    apellido VARCHAR(100),
    edad INT,
    numero VARCHAR(20),
    direccion VARCHAR(200),
    email VARCHAR(100),
    password VARCHAR(100)
);

CREATE TABLE emprendedores (
    id INT PRIMARY KEY,
    nombre_negocio VARCHAR(100),
    direccion_negocio VARCHAR(200),
    FOREIGN KEY (id) REFERENCES usuarios (id) ON DELETE CASCADE
);

CREATE TABLE cliente (
    dni VARCHAR(20) PRIMARY KEY,
    nombre VARCHAR(50),
    apellido VARCHAR(50),
    edad INT,
    numero VARCHAR(20),
    direccion VARCHAR(100),
    calificacion INT
);

CREATE TABLE producto (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100),
    descripcion VARCHAR(200),
    precio DOUBLE,
    stock INT,
    categoria VARCHAR(50)
);

CREATE TABLE pedido (
    id INT AUTO_INCREMENT PRIMARY KEY,
    dni_cliente VARCHAR(20),
    estado VARCHAR(30),
    fecha_pedido DATE,
    FOREIGN KEY (dni_cliente) REFERENCES cliente (dni)
);

CREATE TABLE item_pedido (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT,
    id_producto INT,
    cantidad INT,
    precio DOUBLE,
    FOREIGN KEY (id_pedido) REFERENCES pedido (id),
    FOREIGN KEY (id_producto) REFERENCES producto (id)
);

CREATE TABLE venta (
    id INT PRIMARY KEY, -- MISMO ID QUE PEDIDO
    metodo_pago VARCHAR(50),
    fecha_venta DATE,
    FOREIGN KEY (id) REFERENCES pedido (id)
);