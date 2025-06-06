package org.example.savemate.model;

public class Presupuesto {
    private int idPresupuesto;
    private double montoEstimado;
    private int mes;
    private int anio;
    private int idCuenta;

    public Presupuesto(int idPresupuesto, double montoEstimado, int mes, int anio, int idCuenta) {
        this.idPresupuesto = idPresupuesto;
        this.montoEstimado = montoEstimado;
        this.mes = mes;
        this.anio = anio;
        this.idCuenta = idCuenta;
    }

    public Presupuesto(double montoEstimado, int mes, int anio, int idCuenta) {
        this(0, montoEstimado, mes, anio, idCuenta);
    }

    public int getIdPresupuesto() {
        return idPresupuesto;
    }

    public void setIdPresupuesto(int idPresupuesto) {
        this.idPresupuesto = idPresupuesto;
    }

    public double getMontoEstimado() {
        return montoEstimado;
    }

    public void setMontoEstimado(double montoEstimado) {
        this.montoEstimado = montoEstimado;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public int getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(int idCuenta) {
        this.idCuenta = idCuenta;
    }
}