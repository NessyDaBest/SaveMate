package org.example.savemate.model;

import java.time.LocalDate;

public class Gasto {
    private int idGasto;
    private double monto;
    private LocalDate fecha;
    private String descripcion;
    private int idCuenta;

    public Gasto(int idGasto, double monto, LocalDate fecha, String descripcion, int idCuenta) {
        this.idGasto = idGasto;
        this.monto = monto;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.idCuenta = idCuenta;
    }

    // Getters
    public int getIdGasto() { return idGasto; }
    public double getMonto() { return monto; }
    public LocalDate getFecha() { return fecha; }
    public String getDescripcion() { return descripcion; }
    public int getIdCuenta() { return idCuenta; }
}