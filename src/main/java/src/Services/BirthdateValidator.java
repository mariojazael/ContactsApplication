package src.Services;

public class BirthdateValidator implements Validator {

    @Override
    public boolean validate(String birthdate) {
        String[] birthDateParts = birthdate.split("-");
        if(birthDateParts.length == 3) return validateBirthDateParts(birthDateParts);
        else return false;
    }

    public boolean validateBirthDateParts(String[] birthDateParts) {
        return birthDateParts[0].length() == 4
                && Integer.parseInt(birthDateParts[0]) > 0
                && birthDateParts[1].length() == 2
                && Integer.parseInt(birthDateParts[1]) > 0
                && Integer.parseInt(birthDateParts[1]) <= 12
                && birthDateParts[2].length() == 2
                && Integer.parseInt(birthDateParts[2]) > 0
                && Integer.parseInt(birthDateParts[2]) <= 30;
    }
}
