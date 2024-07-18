package src;

import src.Factories.AbstractFactory;
import src.Factories.ContactsFactory;
import src.Factories.OrganizationsFactory;
import src.Models.AbstractEntity;
import src.Models.Contact;
import src.Validators.BirthdateValidator;
import src.Validators.GenderValidator;
import src.Validators.PhoneNumberValidator;

import static src.MenuOptions.*;
import static src.Services.DateFormaterService.getFormattedCurrentDate;

import java.lang.reflect.Field;
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
    private static final String PERSON = "person";
    private static final String ORGANIZATION = "organization";

    public void start() {
        boolean flag = true;
        while (flag) {
            showMenu();
            MenuOptions option = askForOption();
            flag = switchInput(option, Optional.empty());
        }
    }

    private MenuOptions askForOption() {
        return MenuOptions.valueOf(Scanner.nextLine().toUpperCase());
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
        if(type.equals(PERSON)) {
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
        } else if(type.equals(ORGANIZATION)) {
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
        return type.equals(PERSON) ? contactsFactory : organizationsFactory;
    }

    private boolean switchInput(MenuOptions option, Optional<Integer> optionalId) {
        return switch (option) {
            case ADD -> add();
            case LIST -> list(option);
            case EDIT -> edit(optionalId);
            case SEARCH -> search(option);
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

    private boolean list(MenuOptions option) {
        listRecords();
        int id = Integer.parseInt(askForDesiredId());
        id = findActualId(id);
        processId(option, id);
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

    private boolean search(MenuOptions option) {
        String query = askForSearchQuery( );
        searchWithQuery(query).ifPresentOrElse(results -> {
            showResults(results);
            AtomicReference<Object> action = new AtomicReference<>(askForAction(option));
            if(!action.get().equals(BACK) && !action.get().equals(AGAIN)) {
                int id = (int) action.get();
                id = findActualId(results, id);
                processId(EDIT, id);
            } else if(action.get().equals(AGAIN)) switchInput(SEARCH, Optional.empty());
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
        if(type.equals(PERSON)) {
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
        } else if(type.equals(ORGANIZATION)) {
            if (!phoneNumberValidator.validate(info[4])) {
                System.out.println("Wrong number format!");
                info[2] = "[no number]";
            }
        }
    }

    /*
        This procedure allows to perform editing and deleting operations on a single record as much as we
        want to. It takes a menu option as first parameter that will help to figure out what the
        available actions for us are. This method calls a method 'askForAction' that is intended to be reused
        by multiple business logic operations; that is the reason why it is needed to determine what we are
        pretending to do. In this case, the 'processId' method is supposed to work with one record since the
        second parameter is an id, so the 'option' parameter will show the actions available when it comes about
        processing one single id.
     */
    private void processId(MenuOptions option, int id) {
        getRecordById(id).ifPresentOrElse(record -> {
            AtomicReference<MenuOptions> action = new AtomicReference<>(null);
            final AtomicBoolean flag = new AtomicBoolean(true);
            do {
                System.out.println(record);
                action.set((MenuOptions) askForAction(option));
                switch (action.get()) {
                    case EDIT : {
                        flag.set(switchInput(EDIT, Optional.of(id)));
                        break;
                    }
                    // It is pretended to set flag to false either DELETE or MENU is selected, so 'break' statement can be bypassed in this case.
                    case DELETE : switchInput(REMOVE, Optional.of(id));
                    case MENU : {
                        flag.set(false);
                        break;
                    }
                    case DEFAULT: System.out.println("Invalid action!");
                }
            } while(flag.get());
            System.out.println();
        }, () -> System.out.println("No record found.\n"));
    }

    private int findActualId(List<AbstractEntity> results, int id) {
        return results.get(id - 1).getId();
    }

    private int findActualId(int id) {
        return findActualId(Records, id);
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

    private Object askForAction(MenuOptions option) {
        switch(option) {
            case EDIT :
            case LIST : {
                System.out.println("[record] Enter action (edit, delete, menu):");
                break;
            }
            case SEARCH: {
                System.out.println("[search] Enter action ([number], back, again):");
                break;
            }
            default: {
                System.out.println("Bad option!");
                break;
            }
        }
        String action = Scanner.nextLine();
        int id;
        try {
            id = Integer.parseInt(action);
        } catch (NumberFormatException e) {
            return MenuOptions.valueOf(action.toUpperCase());
        }
        return id;
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
            if(!phoneNumberValidator.validate(record.getNumber())) record.setNumber("[no number]");
            System.out.println(i++ + ". " + record.getName() + ", " + record.getNumber());
        }
    }

    private String askForDesiredId() {
        System.out.print("Select the record id: ");
        return Scanner.nextLine();
    }

    private Optional<AbstractEntity> getRecordById(int id) {
        return Records.stream()
                .filter(record -> record.getId() == id)
                .findFirst();
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
}
