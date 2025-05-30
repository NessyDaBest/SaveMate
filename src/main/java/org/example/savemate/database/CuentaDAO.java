package org.example.savemate.database;

import org.example.savemate.model.Cuenta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CuentaDAO {

    public static Cuenta obtenerCuentaPorUsuario(int idUsuario) {
        String query = "SELECT id_cuenta, nombre, banco, saldo_inicial FROM cuenta WHERE id_usuario = ? LIMIT 1";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Cuenta(
                        rs.getInt("id_cuenta"),
                        rs.getString("nombre"),
                        rs.getString("banco"),
                        rs.getDouble("saldo_inicial")
                );
            }

        } catch (Exception e) {
            System.err.println("Error al obtener la cuenta del usuario: " + e.getMessage());
        }

        return null;
    }
}