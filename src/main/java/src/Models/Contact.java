package src.Models;

public class Contact extends AbstractEntity{
    private String surname;
    private String birthDate;
    private String gender;

    public Contact(int id, String firstName, String lastName, String birthDate, String gender, String phone) {
        super(id, firstName + " " + lastName, phone);
        this.surname = lastName;
        this.birthDate = birthDate;
        this.gender = gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getFullName() {
        return getName() + " " + getSurname();
    }

    @Override
    public String toString() {
        return "Name: " + getName() + "\n" +
            "Surname: " + getSurname() + "\n" +
            "Birth date: " + getBirthDate() + "\n" +
            "Gender: " + getGender() + "\n" +
            "Number: " + getNumber() + "\n" +
            "Time created: " + getTimeCreated() + "\n" +
            "Time last edit: " + getTimeLastModified() + "\n";
    }
}
