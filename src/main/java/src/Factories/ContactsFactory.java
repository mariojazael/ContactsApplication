package src.Factories;

import src.Models.AbstractEntity;
import src.Models.Contact;

public class ContactsFactory implements AbstractFactory<Contact> {
    // String firstName, String lastName, String birth date, String gender, String number
    @Override
    public Contact create(String... values) {
        return new Contact(AbstractEntity.count++, values[0], values[1], values[2], values[3], values[4]);
    }
}
