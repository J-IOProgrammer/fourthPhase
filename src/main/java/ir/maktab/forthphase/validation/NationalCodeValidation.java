package ir.maktab.forthphase.validation;

public class NationalCodeValidation {
    public static boolean isValidNationalCode(String nationalCode) {
        if (nationalCode.length() > 10)
            return true;
        if (nationalCode.length() >= 8 && nationalCode.length() < 10) {
            if (nationalCode.length() == 8)
                nationalCode = "00" + nationalCode;
            else
                nationalCode = "0" + nationalCode;
        }
        String[] numbers = nationalCode.split("");
        int flag = 10;
        long sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (long) Integer.parseInt(numbers[i]) * flag;
            flag--;
        }
        long result = sum % 11;
        if (result < 2) {
            return Long.parseLong(numbers[9]) != result;
        } else {
            return Long.parseLong(numbers[9]) != (11 - result);
        }
    }
}
