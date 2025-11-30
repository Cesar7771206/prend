package com.poo.prend.model.database.dao;

import com.poo.prend.model.database.ConnectionDB;
import com.poo.prend.model.entities.Cliente;
import com.poo.prend.model.entities.ItemPedido;
import com.poo.prend.model.entities.Pedido;
import com.poo.prend.model.entities.Producto;
import com.poo.prend.model.enums.Estado;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    public void crearPedido(Pedido pedido, int emprendimientoId) throws SQLException {
        String sqlPedido = "INSERT INTO pedidos (id_emprendimiento, id_cliente, fecha_pedido, estado, total) VALUES (?, ?, ?, ?, ?)";
        String sqlItems = "INSERT INTO items_pedido (pedido_id, producto_id, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";
        
        Connection conn = null;
        try {
            conn = ConnectionDB.getConnection();
            conn.setAutoCommit(false); 

            // 1. Cabecera
            int pedidoId = 0;
            try (PreparedStatement ps = conn.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, emprendimientoId);
                ps.setInt(2, pedido.getCliente().getId());
                ps.setTimestamp(3, new Timestamp(pedido.getFechaPedido().getTime()));
                ps.setString(4, pedido.getEstado().toString());
                
                double total = 0;
                if (pedido.getItems() != null) {
                    total = pedido.getItems().stream()
                            .mapToDouble(i -> i.getCantidad() * i.getPrecioUnitario())
                            .sum();
                }
                ps.setDouble(5, total);
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        pedidoId = rs.getInt(1);
                        pedido.setId(pedidoId);
                    }
                }
            }

            // 2. Items
            if (pedido.getItems() != null && !pedido.getItems().isEmpty()) {
                try (PreparedStatement psItem = conn.prepareStatement(sqlItems)) {
                    for (ItemPedido item : pedido.getItems()) {
                        psItem.setInt(1, pedidoId);
                        psItem.setInt(2, item.getProducto().getId());
                        psItem.setInt(3, item.getCantidad());
                        psItem.setDouble(4, item.getPrecioUnitario());
                        psItem.addBatch();
                    }
                    psItem.executeBatch();
                }
            }
            conn.commit(); 
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.setAutoCommit(true);
        }
    }

    public List<Pedido> listarPedidos(int emprendimientoId) throws SQLException {
        // Devuelve TODOS los pedidos (útil para cálculos generales)
        return listarPedidosQuery("SELECT p.id, p.fecha_pedido, p.estado, p.total, c.id as cliente_id, c.nombre, c.apellido, c.dni " +
                                  "FROM pedidos p JOIN clientes c ON p.id_cliente = c.id " +
                                  "WHERE p.id_emprendimiento = ? ORDER BY p.fecha_pedido DESC", emprendimientoId);
    }
    
    // Método para filtrar por estado (PENDIENTE vs ENTREGADO/COMPLETADO)
    public List<Pedido> listarPedidosPorEstado(int emprendimientoId, String estadoExcluido) throws SQLException {
        String sql = "SELECT p.id, p.fecha_pedido, p.estado, p.total, c.id as cliente_id, c.nombre, c.apellido, c.dni " +
                     "FROM pedidos p JOIN clientes c ON p.id_cliente = c.id " +
                     "WHERE p.id_emprendimiento = ? AND p.estado != ? ORDER BY p.fecha_pedido DESC";
         // Lógica inversa: Si paso 'ENTREGADO', traigo los PENDIENTES, etc.
         // Para simplificar, haremos filtrado en Java o consultas específicas si es necesario.
         // Usemos el método genérico y filtremos en controller para flexibilidad.
         return listarPedidos(emprendimientoId);
    }

    private List<Pedido> listarPedidosQuery(String sql, int emprendimientoId) throws SQLException {
        List<Pedido> pedidos = new ArrayList<>();
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, emprendimientoId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Pedido p = new Pedido();
                p.setId(rs.getInt("id"));
                p.setFechaPedido(rs.getTimestamp("fecha_pedido"));
                try {
                    p.setEstado(Estado.valueOf(rs.getString("estado").toUpperCase()));
                } catch (Exception e) {
                    p.setEstado(Estado.PENDIENTE);
                }
                
                Cliente c = new Cliente();
                c.setId(rs.getInt("cliente_id"));
                c.setNombre(rs.getString("nombre"));
                c.setApellido(rs.getString("apellido"));
                c.setDNI(rs.getString("dni"));
                p.setCliente(c);
                
                // Cargar Items
                p.setItems(listarItemsPorPedido(p.getId(), conn));
                pedidos.add(p);
            }
        }
        return pedidos;
    }

    private List<ItemPedido> listarItemsPorPedido(int pedidoId, Connection conn) throws SQLException {
        List<ItemPedido> items = new ArrayList<>();
        String sql = "SELECT i.id, i.cantidad, i.precio_unitario, p.id as prod_id, p.nombre, p.precio, p.stock " +
                     "FROM items_pedido i JOIN productos p ON i.producto_id = p.id WHERE i.pedido_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pedidoId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Producto prod = new Producto();
                prod.setId(rs.getInt("prod_id"));
                prod.setNombre(rs.getString("nombre"));
                prod.setPrecio(rs.getDouble("precio"));
                prod.setStock(rs.getInt("stock"));

                ItemPedido item = new ItemPedido(rs.getInt("id"), prod, rs.getInt("cantidad"), rs.getDouble("precio_unitario"));
                items.add(item);
            }
        }
        return items;
    }

    public void actualizarEstado(int pedidoId, Estado nuevoEstado) throws SQLException {
        String sql = "UPDATE pedidos SET estado = ? WHERE id = ?";
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado.toString());
            ps.setInt(2, pedidoId);
            ps.executeUpdate();
        }
    }
    
    public void eliminarPedido(int idPedido) throws SQLException {
        String sqlItems = "DELETE FROM items_pedido WHERE pedido_id = ?";
        String sqlVentas = "DELETE FROM ventas WHERE pedido_id = ?";
        String sqlPedido = "DELETE FROM pedidos WHERE id = ?";

        try (Connection conn = ConnectionDB.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(sqlItems);
                 PreparedStatement ps2 = conn.prepareStatement(sqlVentas);
                 PreparedStatement ps3 = conn.prepareStatement(sqlPedido)) {
                ps1.setInt(1, idPedido); ps1.executeUpdate();
                ps2.setInt(1, idPedido); ps2.executeUpdate();
                ps3.setInt(1, idPedido); ps3.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}