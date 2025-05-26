package org.example.savemate.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {
    public static String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean check(String plain, String hashed) {
        return BCrypt.checkpw(plain, hashed);
    }
}