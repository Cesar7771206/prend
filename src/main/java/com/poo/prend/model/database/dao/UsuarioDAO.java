package com.poo.prend.model.database.dao;

import com.poo.prend.model.database.ConnectionDB;
import com.poo.prend.model.entities.Usuario;
import java.sql.*;

public class UsuarioDAO {

    public void crearUsuario(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (dni, nombre, apellido, edad, numero_telefono, direccion, correo, contrasena) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, usuario.getDNI());
            ps.setString(2, usuario.getNombre());
            ps.setString(3, usuario.getApellido());
            ps.setInt(4, usuario.getEdad());
            ps.setString(5, usuario.getNumero()); 
            ps.setString(6, usuario.getDireccion());
            ps.setString(7, usuario.getCorreo());
            ps.setString(8, usuario.getContraseña());
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Error al crear usuario, no se guardaron filas.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    usuario.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Error al crear usuario, no se obtuvo el ID.");
                }
            }
        }
    }

    public Usuario login(String correo, String contrasena) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE correo = ? AND contrasena = ?";
        Usuario usuario = null;

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, correo);
            ps.setString(2, contrasena);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuario(
                        rs.getInt("id"),
                        rs.getString("correo"),
                        rs.getString("contrasena"),
                        null 
                    );
                    
                    usuario.setDNI(rs.getString("dni"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setApellido(rs.getString("apellido"));
                    usuario.setEdad(rs.getInt("edad"));
                    usuario.setNumero(rs.getString("numero_telefono")); 
                    usuario.setDireccion(rs.getString("direccion"));
                }
            }
        }
        return usuario;
    }
}