package src.Services;

@FunctionalInterface
public interface AbstractFactory<T> {
    T create(String... values);
    static boolean validatePhoneNumber(String phoneNumber) {
        return phoneNumber.matches("\\+?\\([^\\W_]+\\)([- ][^\\W_]{2,})*")
                || phoneNumber.matches("\\+?[^\\W_]+([- ]\\([^\\W_]{2,}\\))*([- ][^\\W_]{2,})*");
    }
}
