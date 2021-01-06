package entities;

import lombok.Builder;

@Builder
public class Dog {

    private final int id;
    private final String name;
    private final String breed;
    private double weight;

    public Dog(int id, String name, String breed, double weight) {
        this.id = id;
        this.name = name;
        this.breed = breed;
        this.weight = weight;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBreed() {
        return breed;
    }

    public double getWeight() {
        return weight;
    }

    public void feed() {
        this.weight *= 1.01;
    }

    @Override
    public String toString() {
        return "Dog{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", breed='" + breed + '\'' +
                ", weight=" + weight +
                '}';
    }
}
