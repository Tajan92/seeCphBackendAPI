package app.utils;

import app.dto.user.UserRegisterDTO;
import java.util.ArrayList;
import java.util.List;

public class UserValidator { // TODO: Needs a lot of work to take in validation annotation
    public List<String> validate(UserRegisterDTO userRegisterDTO) {
        List<String> message = new ArrayList<>();

        if (userRegisterDTO == null) {
            message.add("All fields are required");
            return message;
        }

        String email = userRegisterDTO.getEmail();
        String phone = userRegisterDTO.getPhone();
        String password = userRegisterDTO.getPassword();
        String passwordCheck = userRegisterDTO.getPasswordCheck();

        validatePhoneNumber(phone, message);
        passwordMustContainNumber(password, message);
        shouldRejectPasswordWithoutSpecialCharacter(password, message);
        passwordsMustMatch(password, passwordCheck, message);
        validateEmail(email, message);
        return message;
    }

    private void passwordsMustMatch(String password, String passwordCheck, List<String> message) {
        if (!password.equals(passwordCheck)) {
            message.add("Passwords do not match");
        }
    }

    private void passwordMustContainNumber(String password, List<String> message) {
        if (!password.chars().anyMatch(Character::isDigit)) {
            message.add("Password must contain a number");
        }
    }

    private void shouldRejectPasswordWithoutSpecialCharacter(String password, List<String> message) {
        if (!password.chars().anyMatch(c -> !Character.isLetterOrDigit(c))) {
            message.add("Password must contain a special character");
        }
    }

    private void validateEmail(String email, List<String> message) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!email.matches(emailRegex)) {
            message.add("Email-format not right");
        }
    }

    private void validatePhoneNumber(String phoneNumber, List<String> message) {
        String phoneRegexDK = "^[0-9]{8}$";
        String phoneNumberRegex = "^(?:\\+|00)[1-9][0-9]{1,2}[0-9]{4,12}$";
        if (!phoneNumber.matches(phoneNumberRegex) && !phoneNumber.matches(phoneRegexDK)) {
            message.add("Phone number format not right");
        }
    }
}
