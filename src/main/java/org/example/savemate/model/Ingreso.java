package org.example.savemate.model;

import java.time.LocalDate;

public class Ingreso {
    private int idIngreso;
    private int idCuenta;
    private String descripcion;
    private double monto;
    private LocalDate fecha;

    public Ingreso(int idIngreso, int idCuenta, String descripcion, double monto, LocalDate fecha) {
        this.idIngreso = idIngreso;
        this.idCuenta = idCuenta;
        this.descripcion = descripcion;
        this.monto = monto;
        this.fecha = fecha;
    }

    public int getIdIngreso() {
        return idIngreso;
    }

    public void setIdIngreso(int idIngreso) {
        this.idIngreso = idIngreso;
    }

    public int getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(int idCuenta) {
        this.idCuenta = idCuenta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
}