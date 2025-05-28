package org.example.savemate.util;

public class UserSession {

    private static String nombre;
    private static String email;

    public static void iniciarSesion(String nombreUsuario, String emailUsuario) {
        nombre = nombreUsuario;
        email = emailUsuario;
    }

    public static String getNombre() {
        return nombre;
    }

    public static String getEmail() {
        return email;
    }

    public static void cerrarSesion() {
        nombre = null;
        email = null;
    }
}