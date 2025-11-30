package com.poo.prend.model.database.dao;

import com.poo.prend.model.database.ConnectionDB;
import com.poo.prend.model.entities.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public void guardarCliente(Cliente cliente, int emprendimientoId) throws SQLException {
        String sql = "INSERT INTO clientes (dni, nombre, apellido, numero_telefono, direccion, calificacion, id_emprendimiento) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cliente.getDNI());
            ps.setString(2, cliente.getNombre());
            ps.setString(3, cliente.getApellido());
            ps.setString(4, cliente.getNumero());
            ps.setString(5, cliente.getDireccion());
            ps.setInt(6, cliente.getCalificacion());
            ps.setInt(7, emprendimientoId); 
            ps.executeUpdate();
        }
    }

    // --- NUEVO MÉTODO PARA EDITAR ---
    public void actualizarCliente(Cliente cliente) throws SQLException {
        String sql = "UPDATE clientes SET dni=?, nombre=?, apellido=?, numero_telefono=?, direccion=?, calificacion=? WHERE id=?";
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cliente.getDNI());
            ps.setString(2, cliente.getNombre());
            ps.setString(3, cliente.getApellido());
            ps.setString(4, cliente.getNumero());
            ps.setString(5, cliente.getDireccion());
            ps.setInt(6, cliente.getCalificacion());
            ps.setInt(7, cliente.getId());
            ps.executeUpdate();
        }
    }
    
    public void eliminarCliente(int idCliente) throws SQLException {
        String sql = "DELETE FROM clientes WHERE id = ?";
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            ps.executeUpdate();
        }
    }

    public List<Cliente> listarClientes(int emprendimientoId) throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes WHERE id_emprendimiento = ?";
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, emprendimientoId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Cliente c = new Cliente(
                    rs.getInt("calificacion"),
                    new ArrayList<>(), 
                    new ArrayList<>() 
                );
                c.setId(rs.getInt("id"));
                c.setDNI(rs.getString("dni"));
                c.setNombre(rs.getString("nombre"));
                c.setApellido(rs.getString("apellido"));
                c.setNumero(rs.getString("numero_telefono"));
                c.setDireccion(rs.getString("direccion"));
                clientes.add(c);
            }
        }
        return clientes;
    }
}