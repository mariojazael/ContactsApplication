package src.Factories;

import src.Models.AbstractEntity;
import src.Models.Organization;

public class OrganizationsFactory implements AbstractFactory<Organization> {
    @Override
    public Organization create(String... values) {
        return new Organization(AbstractEntity.count++, values[0], values[1], values[2]);
    }
}
