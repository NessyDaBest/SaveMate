package org.example.savemate.util;

import org.example.savemate.model.Cuenta;
import org.example.savemate.model.Usuario;

public class Sesion {
    private static Usuario usuarioActual;
    private static Cuenta cuentaActual;

    public static void iniciarSesion(Usuario usuario) {
        usuarioActual = usuario;
    }

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public static void cerrarSesion() {
        usuarioActual = null;
    }

    public static void setCuentaActual(Cuenta cuenta) {
        cuentaActual = cuenta;
    }

    public static Cuenta getCuentaActual() {
        return cuentaActual;
    }
}