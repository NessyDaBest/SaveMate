package org.example.savemate.database;

import org.example.savemate.database.DatabaseConnector;
import org.example.savemate.model.Gasto;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class GastoDAO {

    public static void insertarGasto(Gasto gasto) {
        String sql = "INSERT INTO gasto (monto, fecha, descripcion, id_cuenta) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, gasto.getMonto());
            stmt.setDate(2, java.sql.Date.valueOf(gasto.getFecha()));
            stmt.setString(3, gasto.getDescripcion());
            stmt.setInt(4, gasto.getIdCuenta());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void actualizarGasto(Gasto gasto) {
        String sql = "UPDATE gasto SET monto = ?, fecha = ?, descripcion = ? WHERE id_gasto = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, gasto.getMonto());
            stmt.setDate(2, java.sql.Date.valueOf(gasto.getFecha()));
            stmt.setString(3, gasto.getDescripcion());
            stmt.setInt(4, gasto.getIdGasto());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void eliminarGasto(int idGasto) {
        String sql = "DELETE FROM gasto WHERE id_gasto = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idGasto);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}