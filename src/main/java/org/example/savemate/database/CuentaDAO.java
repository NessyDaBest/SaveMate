package org.example.savemate.database;

import org.example.savemate.model.Cuenta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

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
    public static Map<Integer, Double> obtenerGastosMensuales(int idCuenta, int anio) {
        Map<Integer, Double> gastosPorMes = new HashMap<>();

        String query = """
                SELECT EXTRACT(MONTH FROM fecha) AS mes, SUM(monto) AS total
                FROM gasto
                WHERE id_cuenta = ? AND EXTRACT(YEAR FROM fecha) = ?
                GROUP BY mes ORDER BY mes;
                """;

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idCuenta);
            stmt.setInt(2, anio);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int mes = rs.getInt("mes"); // 1 a 12
                double total = rs.getDouble("total");
                gastosPorMes.put(mes, total);
            }

        } catch (Exception e) {
            System.err.println("Error leyendo gastos mensuales: " + e.getMessage());
        }

        return gastosPorMes;
    }
    public static double obtenerTotalGastosAnuales(int idCuenta, int anio) {
        String query = "SELECT SUM(monto) FROM gasto WHERE id_cuenta = ? AND EXTRACT(YEAR FROM fecha) = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idCuenta);
            stmt.setInt(2, anio);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (Exception e) {
            System.err.println("Error obteniendo total de gastos: " + e.getMessage());
        }
        return 0;
    }
    public static double obtenerTotalGastosAcumulado(int idCuenta) {
        String query = "SELECT SUM(monto) FROM gasto WHERE id_cuenta = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idCuenta);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (Exception e) {
            System.err.println("Error obteniendo gastos acumulados: " + e.getMessage());
        }
        return 0;
    }


    public static Map<Integer, Double> obtenerIngresosMensuales(int idCuenta, int anio) {
        Map<Integer, Double> ingresosPorMes = new HashMap<>();

        String query = """
        SELECT EXTRACT(MONTH FROM fecha) AS mes, SUM(monto) AS total
        FROM ingreso
        WHERE id_cuenta = ? AND EXTRACT(YEAR FROM fecha) = ?
        GROUP BY mes ORDER BY mes;
        """;

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idCuenta);
            stmt.setInt(2, anio);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int mes = rs.getInt("mes");
                double total = rs.getDouble("total");
                ingresosPorMes.put(mes, total);
            }

        } catch (Exception e) {
            System.err.println("Error leyendo ingresos mensuales: " + e.getMessage());
        }

        return ingresosPorMes;
    }
    public static double obtenerTotalIngresosAnuales(int idCuenta, int anio) {
        String query = "SELECT SUM(monto) FROM ingreso WHERE id_cuenta = ? AND EXTRACT(YEAR FROM fecha) = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idCuenta);
            stmt.setInt(2, anio);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (Exception e) {
            System.err.println("Error obteniendo total de ingresos: " + e.getMessage());
        }
        return 0;
    }
    public static double obtenerTotalIngresosAcumulado(int idCuenta) {
        String query = "SELECT SUM(monto) FROM ingreso WHERE id_cuenta = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idCuenta);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (Exception e) {
            System.err.println("Error obteniendo ingresos acumulados: " + e.getMessage());
        }
        return 0;
    }

}