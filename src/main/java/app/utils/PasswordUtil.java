package app.utils;

import app.entities.users.User;
import org.mindrot.jbcrypt.BCrypt;

import java.nio.charset.StandardCharsets;

public class PasswordUtil {

    public static String hashPassword(String password){
        return BCrypt.hashpw(password, BCrypt.gensalt());

    }

    public static boolean verifyPassword(String password, User user){
        return password != null
                && password.getBytes(StandardCharsets.UTF_8).length <= 72
                && BCrypt.checkpw(password, user.getHashedPassword());
    }
}