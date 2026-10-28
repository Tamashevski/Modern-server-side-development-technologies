package com.gson.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gson.model.Car;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WriteExample {
    public static void main(String[] args) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        List<Car> cars = createCars();
        final String jsonData = gson.toJson(cars);

        try (FileWriter writer = new FileWriter("cars.json")) {
            writer.write(jsonData);
            System.out.println("Записано автомобилей: " + cars.size() + " -> cars.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Car> createCars() {
        List<Car> cars = new ArrayList<>();
        cars.add(new Car("Toyota", "Camry", 2020, 85000, 42000));
        cars.add(new Car("BMW", "X5", 2016, 210000, 118000));
        cars.add(new Car("Kia", "Rio", 2022, 450000, 15000));
        cars.add(new Car("Volkswagen", "Polo", 2019, 55000, 67000));
        cars.add(new Car("Hyundai", "Tucson", 2021, 75000, 28000));
        return cars;
    }
}



//expose
