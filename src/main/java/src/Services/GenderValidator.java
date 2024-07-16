package src.Services;

public class GenderValidator implements Validator{
    @Override
    public boolean validate(String gender) {
        return gender.equals("M") || gender.equals("F");
    }
}
