package src.Factories;

@FunctionalInterface
public interface AbstractFactory<T> {
    T create(String... values);
}
