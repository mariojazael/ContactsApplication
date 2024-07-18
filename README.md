# Contacts app

This application serves as a contact manager administrator. Inside of it, we can add, update, search for or
delete records. We can add two kind of records: persons and organizations. Whenever we adding records we will
be asked for the type of record to be added; then the suitable fields will asked to be filled.

## Application's structure

The application's structure was designed to work with polymorphism since it is needed to add multiple type of
entities. inside of "src/main/java/src/Models" we will find the following file classes:

| Class/Interface Name | Description                                                                                                                                                                                                                                                       |
|----------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `AbstractEntity`     | This is an abstract base class for entities that the program will store. It contains common fields shared by any derived class, such as `id`, `number`, or `name`. It also maintains a counter to manage the identification of entities created within factories. |
| `Contact`            | This class extends `AbstractEntity` and includes additional information specific to contacts, such as `birthdate` and `gender`.                                                                                                                                   |
| `Organization`       | This class extends `AbstractEntity` and adds additional details relevant to organizations, such as `address`.                                                                                                                                                     |


The need of a class that holds the object creation logic for the objects of this application was needed, so a
separate factory for every entity was created, using a common interface for both. Inside of "src/main/java/src/Factories" 
we will find the following classes:

| Class/Interface Name   | Description                                                                                                                                                                                                                                                                                                               |
|------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `AbstractFactory`      | This is a functional interface with a generic type `<T>` representing entities such as `Contact`, `Organization`, or any other entity to be created. It defines the abstract method `create()` which returns an instance of the specified parameter type. It also includes a static method for validating a phone number. |
| `ContactsFactory`      | This class implements the `AbstractFactory` interface and provides custom logic for creating `Contact` instances.                                                                                                                                                                                                         |
| `OrganizationsFactory` | This class implements the `AbstractFactory` interface and provides custom logic for creating `Organization` instances.                                                                                                                                                                                                    |
                                                                                                                                                                       |
Field validation is a separate action from the responsibilities of the previous classes described, however it is an essential step.
Validator classes were added inside of "src/main/java/src/Factories":

| Class/Interface Name   | Description                                                                                                                                                        |
|------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `Validator`            | Common functional interface for classes in charge of validating a specific field.                                                                                  |
| `BirthdateValidator`   | Checks whether the entered birthdate representation matches the pattern `yyyy-mm-dd` or not. Sets value as "[no data]" in case verification fails.                 |
| `GenderValidator`      | Compares the entered input against only two possible values: "M" and "F" without quotes. Sets values as "[no data]" in case the entered value is different.        |
| `PhoneNumberValidator` | Determines if the entered phone matches at least one of two specific phone number patterns. Sets values as "[no number]" in case the entered number doesn't match. |

Summoned the need of providing a date formatting service, an isolated service from the previous classes. Consequently, a service class was added 
inside of "src/main/java/src/Services":

| Class/Interface Name   | Description                                                                                                              |
|------------------------|--------------------------------------------------------------------------------------------------------------------------|
| `DateFormatterService` | This class provides functionality of fetching the current date and time and return it formatted as `yyyy-MM-dd'T'HH:mm`. |


## User interaction

The ContactsApp class displays a menu that looks like this:

`[menu] Enter action (add, list, search, count, exit):`

If we enter the add option, we will be asked to set the type of entity we want to create and then all the
required fields will be requested to fill. number, gender and birthdate fields have an extra validation. In
case any value doesn't pass the validation, its value will be set to "[no data]".

In list case, all the records will be listed and then we will be able to edit, delete records or go back.

In search case, the user will be asked to enter the keywords for the search. This search is performed on the
names and numbers from the records only. All the matching records will be retrieved and we will be able to
edit, delete records or go back.

In count case, the number of records will be printed out.

In exit case, the program finishes its execution.