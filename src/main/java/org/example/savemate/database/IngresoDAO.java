package org.example.savemate.database;

import org.example.savemate.model.Ingreso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class IngresoDAO {

    public static void insertarIngreso(Ingreso ingreso) {
        String sql = "INSERT INTO ingreso (monto, fecha, descripcion, id_cuenta) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, ingreso.getMonto());
            stmt.setDate(2, java.sql.Date.valueOf(ingreso.getFecha()));
            stmt.setString(3, ingreso.getDescripcion());
            stmt.setInt(4, ingreso.getIdCuenta());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void actualizarIngreso(Ingreso ingreso) {
        String sql = "UPDATE ingreso SET monto = ?, fecha = ?, descripcion = ? WHERE id_ingreso = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, ingreso.getMonto());
            stmt.setDate(2, java.sql.Date.valueOf(ingreso.getFecha()));
            stmt.setString(3, ingreso.getDescripcion());
            stmt.setInt(4, ingreso.getIdIngreso());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void eliminarIngreso(int idIngreso) {
        String sql = "DELETE FROM ingreso WHERE id_ingreso = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idIngreso);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}