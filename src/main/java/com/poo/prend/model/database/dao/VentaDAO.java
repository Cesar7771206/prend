package com.poo.prend.model.database.dao;

import com.poo.prend.model.database.ConnectionDB;
import com.poo.prend.model.entities.Venta;
import java.sql.*;

public class VentaDAO {

    // Registrar una venta (Guarda en tabla ventas vinculada a un pedido existente o crea todo junto)
    // Asumiremos que una Venta se registra sobre un Pedido ya finalizado
    public void registrarVenta(Venta venta) throws SQLException {
        String sql = "INSERT INTO ventas (pedido_id, metodo_pago, fecha_venta) VALUES (?, ?, ?)";
        
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, venta.getId()); // ID heredado de Pedido
            ps.setString(2, venta.getMetodoPago().toString());
            ps.setDate(3, new java.sql.Date(venta.getFechaVenta().getTime()));
            
            ps.executeUpdate();
        }
    }

    // Opcional: Obtener total de ventas para el Dashboard
    public double obtenerIngresosTotales(int emprendimientoId) throws SQLException {
        // Requiere JOIN entre ventas, pedidos y items_pedido
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