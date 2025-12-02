package com.poo.prend.model.database.dao;

import com.poo.prend.model.database.ConnectionDB;
import com.poo.prend.model.entities.Venta;
import java.sql.*;

public class VentaDAO {

    public void registrarVenta(Venta venta) throws SQLException {
        String sql = "INSERT INTO ventas (pedido_id, metodo_pago, fecha_venta) VALUES (?, ?, ?)";
        
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, venta.getId());
            ps.setString(2, venta.getMetodoPago().toString());
            ps.setDate(3, new java.sql.Date(venta.getFechaVenta().getTime()));
            
            ps.executeUpdate();
        }
    }

    public double obtenerIngresosTotales(int emprendimientoId) throws SQLException {
        String sql = "SELECT SUM(i.cantidad * i.precio_unitario) as total " +
                     "FROM ventas v " +
                     "JOIN pedidos p ON v.pedido_id = p.id " +
                     "JOIN items_pedido i ON p.id = i.pedido_id " +
                     "WHERE p.emprendimiento_id = ?";
                     
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, emprendimientoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        }
        return 0.0;
    }
}