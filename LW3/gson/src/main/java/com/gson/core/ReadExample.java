package com.gson.core;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gson.model.Car;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;

public class ReadExample {
    private static final int MIN_YEAR = 2018;
    private static final int MAX_MILEAGE = 50000;

    public static void main(String[] args) {
        Gson gson = new Gson();

        Type listType = new TypeToken<List<Car>>() {}.getType();

        try (Reader reader = new FileReader("cars.json")) {
            List<Car> cars = gson.fromJson(reader, listType);
            System.out.println("Прочитано автомобилей: " + cars.size());
            for (Car car : cars) {
                System.out.println(car);
            }

            System.out.println();
            System.out.println("Выпущены после " + MIN_YEAR + " года и пробег меньше " + MAX_MILEAGE + " км:");
            for (Car car : cars) {
                if (car.getYear() > MIN_YEAR && car.getMileage() < MAX_MILEAGE) {
                    System.out.println(car);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
