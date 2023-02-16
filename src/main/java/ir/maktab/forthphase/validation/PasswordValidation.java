package ir.maktab.forthphase.validation;

import java.util.regex.Pattern;

public class PasswordValidation {

    public static boolean isValidatePassword(String password) {
        return Pattern.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}$", password);
    }
}
