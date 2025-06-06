package org.example.savemate.database;

import org.example.savemate.model.Presupuesto;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PresupuestoDAO {

    public static List<Presupuesto> obtenerPresupuestosPorCuenta(int idCuenta) {
        List<Presupuesto> lista = new ArrayList<>();
        String sql = "SELECT * FROM presupuesto WHERE id_cuenta = ? ORDER BY anio, mes";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCuenta);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Presupuesto p = new Presupuesto(
                        rs.getInt("id_presupuesto"),
                        rs.getDouble("monto_estimado"),
                        rs.getInt("mes"),
                        rs.getInt("anio"),
                        rs.getInt("id_cuenta")
                );
                lista.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public static boolean crearPresupuesto(Presupuesto p) {
        String sql = "INSERT INTO presupuesto (monto_estimado, mes, anio, id_cuenta) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, p.getMontoEstimado());
            stmt.setInt(2, p.getMes());
            stmt.setInt(3, p.getAnio());
            stmt.setInt(4, p.getIdCuenta());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean actualizarMontoPresupuesto(int idPresupuesto, double nuevoMonto) {
        String sql = "UPDATE presupuesto SET monto_estimado = ? WHERE id_presupuesto = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, nuevoMonto);
            stmt.setInt(2, idPresupuesto);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean eliminarPresupuesto(int idPresupuesto) {
        String sql = "DELETE FROM presupuesto WHERE id_presupuesto = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPresupuesto);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean presupuestoExiste(int idCuenta, int mes, int anio) {
        String sql = "SELECT COUNT(*) FROM presupuesto WHERE id_cuenta = ? AND mes = ? AND anio = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCuenta);
            stmt.setInt(2, mes);
            stmt.setInt(3, anio);
            ResultSet rs = stmt.executeQuery();

            return rs.next() && rs.getInt(1) > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Map<Integer, Double> obtenerPresupuestosPorCuentaYAño(int idCuenta, int año) {
        Map<Integer, Double> mapa = new HashMap<>();

        String sql = "SELECT mes, monto_estimado FROM presupuesto WHERE id_cuenta = ? AND anio = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCuenta);
            stmt.setInt(2, año);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int mes = rs.getInt("mes");
                double monto = rs.getDouble("monto_estimado");
                mapa.put(mes, monto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mapa;
    }
}