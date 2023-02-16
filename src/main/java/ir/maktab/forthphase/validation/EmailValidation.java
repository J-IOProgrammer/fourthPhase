package ir.maktab.forthphase.validation;

import java.util.regex.Pattern;

public class EmailValidation {

    public static boolean isValidateEmail(String email) {
        return !Pattern.matches("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$", email);
    }
}
