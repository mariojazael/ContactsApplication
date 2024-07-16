package src.Models;

import src.ContactsApp;

public class AbstractEntity {
    private int id;
    private String name;
    private String number;
    String timeCreated;
    String timeLastModified;
    public static int count = 1;

    public AbstractEntity(int id, String name, String number) {
        this.id = id;
        this.name = name;
        this.number = number;
        String time = ContactsApp.getFormattedCurrentDate();
        this.timeCreated = time;
        this.timeLastModified = time;
    }

    public String getTimeLastModified() {
        return timeLastModified;
    }

    public void setTimeLastModified(String timeLastModified) {
        this.timeLastModified = timeLastModified;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}