package com.poo.prend.model.database.dao;

import com.poo.prend.model.database.ConnectionDB;
import com.poo.prend.model.entities.ItemPedido;
import com.poo.prend.model.entities.Pedido;
import com.poo.prend.model.enums.Estado;
import java.sql.*;

public class PedidoDAO {

    public void crearPedido(Pedido pedido, int emprendimientoId) throws SQLException {
        String sqlPedido = "INSERT INTO pedidos (cliente_dni, estado, fecha_pedido, emprendimiento_id) VALUES (?, ?, ?, ?)";
        String sqlItems = "INSERT INTO items_pedido (pedido_id, producto_id, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";
        
        Connection conn = null;
        
        try {
            conn = ConnectionDB.getConnection();
            conn.setAutoCommit(false); // INICIAR TRANSACCIÓN

            // 1. Guardar Pedido
            int pedidoId = 0;
            try (PreparedStatement ps = conn.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, pedido.getCliente().getDNI()); // Usamos DNI como FK del cliente
                ps.setString(2, pedido.getEstado().toString());
                ps.setDate(3, new java.sql.Date(pedido.getFechaPedido().getTime()));
                ps.setInt(4, emprendimientoId);
                ps.executeUpdate();
                
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        pedidoId = rs.getInt(1);
                        pedido.setId(pedidoId);
                    }
                }
            }

            // 2. Guardar Items del Pedido
            try (PreparedStatement psItem = conn.prepareStatement(sqlItems)) {
                for (ItemPedido item : pedido.getItems()) {
                    psItem.setInt(1, pedidoId);
                    psItem.setInt(2, item.getProducto().getId());
                    psItem.setInt(3, item.getCantidad());
                    psItem.setDouble(4, item.getPrecioUnitario());
                    psItem.addBatch(); // Agregamos al lote
                }
                psItem.executeBatch(); // Ejecutamos todo junto
            }

            conn.commit(); // CONFIRMAR TRANSACCIÓN
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // DESHACER SI HAY ERROR
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true); // Restaurar estado por defecto
            }
        }
    }
    
    // Método para cambiar estado (ej. de PENDIENTE a ENTREGADO)
    public void actualizarEstado(int pedidoId, Estado nuevoEstado) throws SQLException {
        String sql = "UPDATE pedidos SET estado = ? WHERE id = ?";
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado.toString());
            ps.setInt(2, pedidoId);
            ps.executeUpdate();
        }
    }
}
