package com.poo.prend.model.entities;

import com.poo.prend.model.enums.Estado; 

import java.util.*; 

public class Pedido {
    private int id; 
    private Cliente cliente; 
    private List<ItemPedido> items; 
    private Estado estado; 
    private Date fechaPedido; 
    
    public Pedido() {}

    public Pedido(int id, Cliente cliente, List<ItemPedido> items, Estado estado, Date fechaPedido) {
        this.id = id;
        this.cliente = cliente;
        this.items = items;
        this.estado = estado;
        this.fechaPedido = fechaPedido;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<ItemPedido> getItems() {
        return items;
    }

    public void setItems(List<ItemPedido> items) {
        this.items = items;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Date getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(Date fechaPedido) {
        this.fechaPedido = fechaPedido;
    }
    
}
