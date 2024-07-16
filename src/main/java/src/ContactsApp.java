package src;

import src.Models.AbstractEntity;
import src.Models.Contact;
import src.Services.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ContactsApp {
    private final Scanner Scanner = new Scanner(System.in);
    private final ContactsFactory contactsFactory = new ContactsFactory();
    private final OrganizationsFactory organizationsFactory = new OrganizationsFactory();
    private final List<AbstractEntity> Records = new ArrayList<>();
    private static final String ROUTE_TO_MODELS = "contacts.models.";
    private final PhoneNumberValidator phoneNumberValidator = new PhoneNumberValidator();
    private final GenderValidator genderValidator = new GenderValidator();
    private final BirthdateValidator birthdateValidator = new BirthdateValidator();

    public void start() {
        boolean flag = true;
        while (flag) {
            showMenu();
            flag = switchInput(Scanner.nextLine(), Optional.empty());
        }
    }

    private void showSuccessAddMessage() {
        System.out.println("The record added.\n");
    }

    private void showSuccessUpdateMessage() {
        System.out.println("The record updated.\n");
    }

    private void showSuccessDeleteMessage() {
        System.out.println("The record removed.\n");
    }

    private void showMenu() {
        System.out.println("[menu] Enter action (add, list, search, count, exit)");
    }

    private String[] askForInfo(String type) {
        if(type.equals("person")) {
            String[] contactInfo = new String[5];
            System.out.println("Enter the name: ");
            contactInfo[0] = Scanner.nextLine();
            System.out.println("Enter the surname: ");
            contactInfo[1] = Scanner.nextLine();
            System.out.println("Enter the birth date:");
            contactInfo[2] = Scanner.nextLine();
            System.out.println("Enter the gender:");
            contactInfo[3] = Scanner.nextLine();
            System.out.println("Enter the number: ");
            contactInfo[4] = Scanner.nextLine();
            return contactInfo;
        } else if(type.equals("organization")) {
            String[] orgInfo = new String[3];
            System.out.println("Enter the organization name: ");
            orgInfo[0] = Scanner.nextLine();
            System.out.println("Enter the address: ");
            orgInfo[1] = Scanner.nextLine();
            System.out.println("Enter the number: ");
            orgInfo[2] = Scanner.nextLine();
            return orgInfo;
        } else return null;
    }

    private void save(String[] info, AbstractFactory<? extends AbstractEntity> factory) {
        AbstractEntity abstractEntity = factory.create(info);
        Records.add(abstractEntity);
    }

    private AbstractFactory<? extends AbstractEntity> solveEntity(String type) {
        return type.equals("person") ? contactsFactory : organizationsFactory;
    }

    private boolean switchInput(String stringOption, Optional<Integer> optionalId) {
        Options option = Options.valueOf(stringOption.toUpperCase());
        return switch (option) {
            case ADD -> add();
            case LIST -> list(stringOption);
            case EDIT -> edit(optionalId);
            case SEARCH -> search(stringOption);
            case REMOVE -> remove(optionalId);
            case COUNT -> count();
            case EXIT -> false;
            default -> defaultCase();
        };
    }

    private boolean add() {
        String type = askForType();
        String[] info = askForInfo(type);
        validateInfo(info, type);
        save(info, solveEntity(type));
        showSuccessAddMessage();
        return true;
    }

    private boolean list(String input) {
        listRecords();
        int id = Integer.parseInt(askForDesiredId());
        processId(input, id);
        return true;
    }

    private boolean edit(Optional<Integer> optionalId) {
        if(Records.isEmpty()) {
            System.out.println("No records to edit.\n");
            return true;
        }
        int id = solveId(optionalId);
        getRecordById(id).ifPresentOrElse(record -> {
            Field field = askForDesiredField(record.getClass());
            if(field != null) {
                String fieldValue = askForFieldValue(field);
                try {
                    field.setAccessible(true);
                    field.set(record, fieldValue);
                    field.setAccessible(false);
                    record.setTimeCreated(getFormattedCurrentDate());
                    showSuccessUpdateMessage();
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            } else System.out.println("Field not found.\n");
        }, () -> System.out.println("Contact id not found.\n") );
        return true;
    }

    private boolean search(String option) {
        String query = askForSearchQuery();
        searchWithQuery(query).ifPresentOrElse(results -> {
            showResults(results);
            String action = askForAction(option);
            if(!action.equals("back") && !action.equals("again")) {
                int id = Integer.parseInt(action);
                id = findActualId(results, id);
                processId("edit", id);
            } else if(action.equals("again")) switchInput("search", Optional.empty());
        }, () -> System.out.println("No record found.\n"));
        return true;
    }

    private boolean remove(Optional<Integer> optionalId) {
        if(Records.isEmpty()) {
            System.out.println("No records to remove.\n");
            return true;
        }
        listRecords();
        int id = solveId(optionalId);
        getRecordById(id).ifPresentOrElse(contact -> {
            Records.remove(contact);
            showSuccessDeleteMessage();
        }, () -> System.out.println("Contact id not found.\n"));
        return true;
    }


    private boolean count() {
        System.out.println("The phone book has " + Records.size() + " records.\n");
        return true;
    }

    private boolean defaultCase() {
        System.out.println("The entered action is not valid.");
        return true;
    }

    private void validateInfo(String[] info, String type) {
        if(type.equals("person")) {
            if(!phoneNumberValidator.validate(info[4])) {
                System.out.println("Wrong number format!");
                info[4] = "[no number]";
            }
            if(!birthdateValidator.validate(info[2])) {
                System.out.println("Bad birth date!");
                info[2] = "[no data]";
            }
            if(!genderValidator.validate(info[3])) {
                System.out.println("Bad gender!");
                info[3] = "[no data]";
            }
        } else if(type.equals("organization")) {
            if (!phoneNumberValidator.validate(info[4])) {
                System.out.println("Wrong number format!");
                info[2] = "[no number]";
            }
        }
    }

    private void processId(String input, int id) {
        AtomicReference<String> action = new AtomicReference<>("");
        final AtomicBoolean flag = new AtomicBoolean(true);
        getRecordById(id).ifPresentOrElse(record -> {
            do {
                System.out.println(record);
                action.set(askForAction(input));
                flag.set(action.get().equals("edit") ? switchInput("edit", Optional.of(id)) :
                        action.get().equals("delete") ? switchInput("remove", Optional.of(id)) :
                                !action.get().equals("menu") && !action.get().equals("back"));
            } while(flag.get());
            System.out.println();
        }, () -> System.out.println("No record found.\n"));
    }

    private int findActualId(List<AbstractEntity> results, int id) {
        return results.get(id - 1).getId();
    }

    private void showResults(List<AbstractEntity> results) {
        AtomicInteger i = new AtomicInteger(1);
        System.out.println("Found " + results.size() + " records:");
        results.forEach(r -> System.out.println(i.getAndIncrement() + ". " + r.getName()));
        System.out.println();
    }

    private Optional<List<AbstractEntity>> searchWithQuery(String query) {
        List<AbstractEntity> list = new ArrayList<>();
        for(AbstractEntity record : Records) {
            if(containsQuery(record, query)) list.add(record);
        }
        return list.isEmpty() ? Optional.empty() : Optional.of(list);
    }

    private boolean containsQuery(AbstractEntity record, String query) {
        String name = record.getName();
        String lowerCaseName = record.getName().toLowerCase();
        String lowerCaseQuery = query.toLowerCase();
        String number = record.getNumber();
        String lowerCaseNumber = record.getNumber().toLowerCase();

        return lowerCaseName.contains(lowerCaseQuery)
                || name.matches(query)
                || record instanceof Contact && (((Contact) record).getFullName().toLowerCase().contains(lowerCaseQuery) || ((Contact) record).getFullName().matches(query))
                || lowerCaseNumber.contains(query.toLowerCase())
                || number.matches(query);
    }

    private int solveId(Optional<Integer> optionalId) {
        int id;
        if(optionalId.isPresent()) id = optionalId.get();
        else {
            listRecords();
            id = Integer.parseInt(askForDesiredId());
        }
        return id;
    }

    private String askForAction(String menu) {
        if(menu.equals("list") || menu.equals("edit")) System.out.println("[record] Enter action (edit, delete, menu):");
        else if(menu.equals("search")) System.out.println("[search] Enter action ([number], back, again):");
        return Scanner.nextLine();
    }

    private String askForSearchQuery() {
        System.out.println("Enter search query: ");
        return Scanner.nextLine();
    }

    private String askForType() {
        System.out.println("Enter the type (person, organization):");
        return Scanner.nextLine();
    }

    private void listRecords() {
        int i = 1;
        for(AbstractEntity record : Records) {
            if(!AbstractFactory.validatePhoneNumber(record.getNumber())) record.setNumber("[no number]");
            System.out.println(i++ + ". " + record.getName() + ", " + record.getNumber());
        }
    }

    private String askForDesiredId() {
        System.out.print("Select the record id: ");
        return Scanner.nextLine();
    }

    private Optional<AbstractEntity> getRecordById(int id) {
        return Optional.ofNullable(Records.get(id));
    }

    private Field askForDesiredField(Class<? extends AbstractEntity> entityClass) {
        displayFieldsMenu(entityClass.getTypeName());
        String field = Scanner.nextLine();
        Optional<Field> optionalField = Arrays.stream(entityClass.getDeclaredFields())
                .filter(f -> f.getName().equals(field))
                .findFirst();
        if(optionalField.isEmpty()) optionalField = Arrays.stream(AbstractEntity.class.getDeclaredFields())
                .filter(f -> f.getName().equals(field))
                .findFirst();
        return optionalField.orElse(null);
    }

    public void displayFieldsMenu(String typeName) {
        switch (typeName) {
            case ROUTE_TO_MODELS + "Contact": {
                System.out.println("Select a field: (name, surname, birth, gender, number): ");
                break;
            }
            case ROUTE_TO_MODELS + "Organization": {
                System.out.println("Select a field: (address, number): ");
                break;
            }
        }
    }

    public String askForFieldValue(Field field) {
        System.out.println("Enter " + field.getName() + ": ");
        return Scanner.nextLine();
    }

    public static String getFormattedCurrentDate() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        return now.format(formatter);
    }
}
