package com.poo.prend.model.database.dao;

import com.poo.prend.model.database.ConnectionDB;
import com.poo.prend.model.entities.Producto;
import com.poo.prend.model.enums.Categoria;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    public void guardarProducto(Producto prod, int emprendimientoId) throws SQLException {
        // CORRECCIÓN: Nombre de columna 'id_emprendimiento'
        String sql = "INSERT INTO productos (nombre, descripcion, precio, stock, categoria, id_emprendimiento) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, prod.getNombre());
            ps.setString(2, prod.getDescripcion());
            ps.setDouble(3, prod.getPrecio());
            ps.setInt(4, prod.getStock());
            ps.setString(5, prod.getCategoria().toString());
            ps.setInt(6, emprendimientoId);
            
            ps.executeUpdate();
            
             try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    prod.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public List<Producto> listarPorEmprendimiento(int emprendimientoId) throws SQLException {
        List<Producto> lista = new ArrayList<>();
        // CORRECCIÓN: Nombre de columna 'id_emprendimiento'
        String sql = "SELECT * FROM productos WHERE id_emprendimiento = ?";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, emprendimientoId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Producto p = new Producto(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getDouble("precio"),
                    rs.getInt("stock"),
                    Categoria.valueOf(rs.getString("categoria"))
                );
                lista.add(p);
            }
        }
        return lista;
    }
    
    // Método solicitado por el MainController para actualizar el producto completo
    public void actualizarProducto(Producto prod) throws SQLException {
        String sql = "UPDATE productos SET nombre=?, descripcion=?, precio=?, stock=?, categoria=? WHERE id=?";
        
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, prod.getNombre());
            ps.setString(2, prod.getDescripcion());
            ps.setDouble(3, prod.getPrecio());
            ps.setInt(4, prod.getStock());
            ps.setString(5, prod.getCategoria().toString());
            ps.setInt(6, prod.getId());
            
            ps.executeUpdate();
        }
    }
    
    // Método específico para actualizar solo stock (opcional, pero útil)
    public void actualizarStock(int idProducto, int nuevoStock) throws SQLException {
        String sql = "UPDATE productos SET stock = ? WHERE id = ?";
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, nuevoStock);
            ps.setInt(2, idProducto);
            ps.executeUpdate();
        }
    }
    
    public void eliminarProducto(int idProducto) throws SQLException {
    String sql = "DELETE FROM productos WHERE id = ?";
    try (Connection conn = ConnectionDB.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, idProducto);
        ps.executeUpdate();
    }
}
}