package org.example.savemate.database;

import org.example.savemate.model.Cuenta;
import org.example.savemate.model.Gasto;
import org.example.savemate.model.Ingreso;
import org.example.savemate.model.Movimiento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public static List<Gasto> listarGastosPorCuenta(int idCuenta) {
        List<Gasto> lista = new ArrayList<>();

        String query = """
        SELECT id_gasto, monto, fecha, descripcion
        FROM gasto
        WHERE id_cuenta = ?
        ORDER BY fecha DESC
    """;

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idCuenta);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Gasto gasto = new Gasto(
                        rs.getInt("id_gasto"),
                        rs.getDouble("monto"),
                        rs.getDate("fecha").toLocalDate(),
                        rs.getString("descripcion"),
                        idCuenta
                );
                lista.add(gasto);
            }

        } catch (Exception e) {
            System.err.println("Error al listar gastos: " + e.getMessage());
        }

        return lista;
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
    public static List<Ingreso> listarIngresosPorCuenta(int idCuenta) {
        List<Ingreso> ingresos = new ArrayList<>();

        String sql = "SELECT id_ingreso, id_cuenta, descripcion, monto, fecha FROM ingreso WHERE id_cuenta = ? ORDER BY fecha DESC";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCuenta);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Ingreso ingreso = new Ingreso(
                        rs.getInt("id_ingreso"),
                        rs.getInt("id_cuenta"),
                        rs.getString("descripcion"),
                        rs.getDouble("monto"),
                        rs.getDate("fecha").toLocalDate()
                );
                ingresos.add(ingreso);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ingresos;
    }

    public static List<Cuenta> listarCuentasDeUsuario(int idUsuario) {
        List<Cuenta> cuentas = new ArrayList<>();

        String query = """
        SELECT id_cuenta, nombre, banco, saldo_inicial
        FROM cuenta
        WHERE id_usuario = ?
        ORDER BY id_cuenta ASC
    """;

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Cuenta cuenta = new Cuenta(
                        rs.getInt("id_cuenta"),
                        rs.getString("nombre"),
                        rs.getString("banco"),
                        rs.getDouble("saldo_inicial")
                );
                cuentas.add(cuenta);
            }

        } catch (Exception e) {
            System.err.println("Error al listar cuentas del usuario: " + e.getMessage());
        }

        return cuentas;
    }

    public static List<Movimiento> listarMovimientosPorCuenta(int idCuenta) {
        List<Movimiento> movimientos = new ArrayList<>();

        String sql = """
        SELECT fecha, descripcion, monto, 'ingreso' as tipo
        FROM ingreso
        WHERE id_cuenta = ?
        UNION ALL
        SELECT fecha, descripcion, monto, 'gasto' as tipo
        FROM gasto
        WHERE id_cuenta = ?
        ORDER BY fecha DESC;
    """;

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCuenta);
            stmt.setInt(2, idCuenta);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                LocalDate fecha = rs.getDate("fecha").toLocalDate();
                String descripcion = rs.getString("descripcion");
                double monto = rs.getDouble("monto");
                boolean esIngreso = rs.getString("tipo").equals("ingreso");

                movimientos.add(new Movimiento(fecha, descripcion, monto, esIngreso));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return movimientos;
    }



    //CRUD CUENTA
    public static boolean crearCuenta(int idUsuario, String nombre,String banco, double saldoInicial) {
        String sql = "INSERT INTO cuenta (id_usuario, nombre, banco, saldo_inicial) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setString(2, nombre);
            stmt.setString(3, banco);
            stmt.setDouble(4, saldoInicial);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error al crear cuenta: " + e.getMessage());
            return false;
        }
    }

    public static Cuenta obtenerCuentaReciente(int idUsuario) {
        String sql = """
        SELECT id_cuenta, nombre, banco, saldo_inicial
        FROM cuenta
        WHERE id_usuario = ?
        ORDER BY id_cuenta DESC
        LIMIT 1
    """;
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
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
            System.err.println("Error al obtener cuenta reciente: " + e.getMessage());
        }
        return null;
    }

    public static double obtenerSaldoInicial(int idCuenta) {
        String sql = "SELECT saldo_inicial FROM cuenta WHERE id_cuenta = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCuenta);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getDouble("saldo_inicial");
        } catch (Exception e) {
            System.err.println("Error leyendo saldo inicial: " + e.getMessage());
        }
        return 0;
    }
    public static boolean actualizarNombreCuenta(int idCuenta, String nuevoNombre) {
        String sql = "UPDATE cuenta SET nombre = ? WHERE id_cuenta = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuevoNombre);
            stmt.setInt(2, idCuenta);
            return stmt.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean eliminarCuenta(int idCuenta) {
        String sql = "DELETE FROM cuenta WHERE id_cuenta = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCuenta);
            return stmt.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Obtener el año mínimo de gastos
    public static int obtenerMinAñoGastos(int idCuenta) {
        String sql = "SELECT COALESCE(MIN(EXTRACT(YEAR FROM fecha)), 0) FROM gasto WHERE id_cuenta = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCuenta);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return (int) rs.getDouble(1);
            }
        } catch (Exception e) {
            System.err.println("Error obteniendo min año gastos: " + e.getMessage());
        }
        return 0;
    }

    // Obtener el año máximo de gastos
    public static int obtenerMaxAñoGastos(int idCuenta) {
        String sql = "SELECT COALESCE(MAX(EXTRACT(YEAR FROM fecha)), 0) FROM gasto WHERE id_cuenta = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCuenta);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return (int) rs.getDouble(1);
            }
        } catch (Exception e) {
            System.err.println("Error obteniendo max año gastos: " + e.getMessage());
        }
        return 0;
    }

    // Obtener el año mínimo de ingresos
    public static int obtenerMinAñoIngresos(int idCuenta) {
        String sql = "SELECT COALESCE(MIN(EXTRACT(YEAR FROM fecha)), 0) FROM ingreso WHERE id_cuenta = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCuenta);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return (int) rs.getDouble(1);
            }
        } catch (Exception e) {
            System.err.println("Error obteniendo min año ingresos: " + e.getMessage());
        }
        return 0;
    }

    // Obtener el año máximo de ingresos
    public static int obtenerMaxAñoIngresos(int idCuenta) {
        String sql = "SELECT COALESCE(MAX(EXTRACT(YEAR FROM fecha)), 0) FROM ingreso WHERE id_cuenta = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCuenta);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return (int) rs.getDouble(1);
            }
        } catch (Exception e) {
            System.err.println("Error obteniendo max año ingresos: " + e.getMessage());
        }
        return 0;
    }

}