Contacts app

This application serves as a contact manager administrator. Inside of it, we can add, update, search for or
delete records. We can add two kind of records: persons and organizations. Whenever we adding records we will
be asked for the type of record to be added; then the suitable fields will asked to be filled.

The application's structure was designed to work with polymorphism since it is needed to add multiple type of
entities. inside "Contacts (Java)/task/src/contacts/models" we will find the following file classes:

| Class Name       | Description                                                                                                                                                                                                                                                       |
|------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `AbstractEntity` | This is an abstract base class for entities that the program will store. It contains common fields shared by any derived class, such as `id`, `number`, or `name`. It also maintains a counter to manage the identification of entities created within factories. |
| `Contact`        | This class extends `AbstractEntity` and includes additional information specific to contacts, such as `birthdate` and `gender`.                                                                                                                                   |
| `Organization`   | This class extends `AbstractEntity` and adds additional details relevant to organizations, such as `address`.                                                                                                                                                     |


The need of a class that holds the object creation logic for the objects of this application was needed, so a
separate factory for every entity was created, using a common interface for both. Inside of "Contacts (Java)
/task/src/contacts/services" we will find the following classes:

| Class/Interface Name   | Description                                                                                                                                                                                                                                                                                                               |
|------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `AbstractFactory`      | This is a functional interface with a generic type `<T>` representing entities such as `Contact`, `Organization`, or any other entity to be created. It defines the abstract method `create()` which returns an instance of the specified parameter type. It also includes a static method for validating a phone number. |
| `ContactsFactory`      | This class implements the `AbstractFactory` interface and provides custom logic for creating `Contact` instances.                                                                                                                                                                                                         |
| `OrganizationsFactory` | This class implements the `AbstractFactory` interface and provides custom logic for creating `Organization` instances.                                                                                                                                                                                                    |
| `ContactsApp`          | This class manages the services available to users, including displaying a menu and handling user interactions.                                                                                                                                                                                                           |

The ContactsApp class displays a menu that looks like this:

[menu] Enter action (add, list, search, count, exit):

If we enter the add option, we will be asked to set the type of entity we want to create and then all the
required fields will be requested to fill. number, gender and birthdate fields have an extra validation. In
case the values doesn't pass the validation, the value will be set to "[no data]".

In list case, all the records will be listed and then we will be able to edit, delete records or go back.

In search case, the user will be asked to enter the keywords for the search. This search is performed on the
names and numbers from the records only. All the matching records will be retrieved and we will be able to
edit, delete records or go back.

In count case, the number of records will be printed out.

In exit case, the program finishes its execution.