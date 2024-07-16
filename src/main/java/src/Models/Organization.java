package src.Models;

public class Organization extends AbstractEntity{
    String address;

    public Organization(int id, String name, String addresss, String number) {
        super(id, name, number);
        this.address = addresss;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Organization name: " + getName() + "\n" +
        "Address: " + getAddress() + "\n" +
        "Number: " + getNumber() + "\n" +
        "Time created: " + getTimeCreated() + "\n" +
        "Time last edit: " + getTimeLastModified() + "\n";
    }
}
