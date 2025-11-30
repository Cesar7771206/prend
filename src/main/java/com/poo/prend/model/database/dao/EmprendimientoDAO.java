package com.poo.prend.model.database.dao;

import com.poo.prend.model.database.ConnectionDB;
import com.poo.prend.model.entities.Emprendimiento;
import java.sql.*;

public class EmprendimientoDAO {

    public void registrarEmprendimiento(Emprendimiento emp, int usuarioId) throws SQLException {
        String sql = "INSERT INTO emprendimientos (id_usuario, nombre, descripcion, rubro) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, usuarioId);
            ps.setString(2, emp.getNombre());
            ps.setString(3, emp.getDescripcion());
            ps.setString(4, "General"); // Valor por defecto o agrega un campo en tu formulario

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    emp.setId(rs.getInt(1));
                }
            }
        }
    }

    public Emprendimiento obtenerPorUsuario(int usuarioId) throws SQLException {
        String sql = "SELECT * FROM emprendimientos WHERE id_usuario = ?";
        Emprendimiento emp = null;

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, usuarioId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    emp = new Emprendimiento(
                        rs.getString("nombre"),
                        rs.getString("descripcion")
                    );
                    emp.setId(rs.getInt("id"));
                    // Si tu objeto Emprendimiento tiene más campos, setéalos aquí
                }
            }
        }
        return emp;
    }
}