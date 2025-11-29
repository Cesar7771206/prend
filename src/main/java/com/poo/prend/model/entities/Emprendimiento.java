package com.poo.prend.model.entities;

import java.util.*;

public class Emprendimiento {
    private String nombre; 
    private String direccion;
    private List<Producto> inventario; 
    private List<Pedido> pedidos; 
    private List<Venta> ventas; 
    private List<Cliente> clientes; 
    
    public Emprendimiento(String nombre, String direccion) {
        this.nombre = nombre;
        this.direccion = direccion;
    }

    public Emprendimiento(String nombre, String direccion, List<Producto> inventario, List<Pedido> pedidos, List<Venta> ventas, List<Cliente> clientes) {
        this.nombre = nombre;
        this.direccion = direccion;
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
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
    
}
