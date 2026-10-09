package app.utils;

public class UserValidator {

    public static boolean passwordsMustMatch(String password, String passwordCheck) {
        return password != null && password.equals(passwordCheck);
    }

    public static boolean passwordMustContainNumber(String password) {
        return password != null && password.chars().anyMatch(Character::isDigit);
    }

    public static boolean shouldRejectPasswordWithoutSpecialCharacter(String password) {
        return password != null && password.chars().anyMatch(c -> !Character.isLetterOrDigit(c));

    }

    public static boolean isPasswordLongEnough(String password) {
        return password != null && password.length() >= 8;
    }

    public static boolean validateEmail(String email) {
        if (email == null) return false;
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }

    public static boolean validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null) return false;
        String phoneRegexDK = "^[0-9]{8}$";
        String phoneNumberRegex = "^(?:\\+|00)[1-9][0-9]{1,2}[0-9]{4,12}$";
        return (phoneNumber.matches(phoneNumberRegex) || phoneNumber.matches(phoneRegexDK));
    }
}
