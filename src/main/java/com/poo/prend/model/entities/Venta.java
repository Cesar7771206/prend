package com.poo.prend.model.entities;

import com.poo.prend.model.enums.MetodoPago;
import java.util.*;

public class Venta extends Pedido {
    private MetodoPago metodoPago;
    private Date fechaVenta;

    public Venta() {
        super();
    }

    public Venta(MetodoPago metodoPago, Date fechaVenta) {
        this.metodoPago = metodoPago;
        this.fechaVenta = fechaVenta;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Date getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(Date fechaVenta) {
        this.fechaVenta = fechaVenta;
    }
}
