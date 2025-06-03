package org.example.savemate.model;

import java.time.LocalDate;

public class Movimiento {
    private LocalDate fecha;
    private String descripcion;
    private double monto;
    private boolean esIngreso; // true = ingreso, false = gasto

    public Movimiento(LocalDate fecha, String descripcion, double monto, boolean esIngreso) {
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.monto = monto;
        this.esIngreso = esIngreso;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getMonto() {
        return monto;
    }

    public boolean isEsIngreso() {
        return esIngreso;
    }
}