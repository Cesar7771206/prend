package com.poo.prend.model.entities;

import java.util.*; 

public class Cliente extends Persona{
    private int id;
    private int calificacion; 
    private List<Pedido> pedidos; 
    private List<Venta> ventas; 
    
    public Cliente(){}

    public Cliente(int calificacion, List<Pedido> pedidos, List<Venta> ventas) {
        this.calificacion = calificacion;
        this.pedidos = pedidos;
        this.ventas = ventas;
    }

    public int getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(int calificacion) {
        this.calificacion = calificacion;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    
}
