package com.poo.prend.model.entities;

import java.util.List;

public class Emprendimiento {
    private int id; 
    private String nombre; 
    private String descripcion; 
    private List<Producto> inventario; 
    private List<Pedido> pedidos; 
    private List<Venta> ventas; 
    private List<Cliente> clientes; 
    
    public Emprendimiento(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion; 
    }

    public Emprendimiento(String nombre, List<Producto> inventario, List<Pedido> pedidos, List<Venta> ventas, List<Cliente> clientes) {
        this.nombre = nombre;
        this.inventario = inventario;
        this.pedidos = pedidos;
        this.ventas = ventas;
        this.clientes = clientes;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Producto> getInventario() {
        return inventario;
    }

    public void setInventario(List<Producto> inventario) {
        this.inventario = inventario;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    public List<Venta> getVentas() {
        return ventas;
    }

    public void setVentas(List<Venta> ventas) {
        this.ventas = ventas;
    }

    public List<Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    
}