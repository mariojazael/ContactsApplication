package src.Services;

public class PhoneNumberValidator implements Validator {
    @Override
    public boolean validate(String phoneNumber) {
        return phoneNumber.matches("\\+?\\([^\\W_]+\\)([- ][^\\W_]{2,})*")
                || phoneNumber.matches("\\+?[^\\W_]+([- ]\\([^\\W_]{2,}\\))*([- ][^\\W_]{2,})*");
    }
}
