package com.jaxb.test;

import com.jaxb.model.Car;
import com.jaxb.model.Department;
import com.jaxb.model.Organization;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class TestExample {
    private static final String XML_FILE = "org-info.xml";
    private static final int MIN_YEAR = 2018;
    private static final int MAX_MILEAGE = 50000;

    public static void main(String[] args) {
        Organization org = createOrganization();

        try {
            JAXBContext context = JAXBContext.newInstance(Organization.class);

            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(org, System.out);
            File outFile = new File(XML_FILE);
            m.marshal(org, outFile);
            System.out.println("Write to file: " + outFile.getAbsolutePath());

            Unmarshaller um = context.createUnmarshaller();
            Organization orgFromFile = (Organization) um.unmarshal(new FileReader(XML_FILE));

            System.out.println();
            System.out.println("Read from file:");
            System.out.println("Organization [" + orgFromFile.getOrganizationId() + "]: "
                    + orgFromFile.getOrganizationName() + ", " + orgFromFile.getAddress());
            for (Department dept : orgFromFile.getDepartments()) {
                System.out.println("  Department " + dept.getDepartmentNumber() + ": "
                        + dept.getDepartmentName() + " (" + dept.getLocation() + ")");
                for (Car car : dept.getCars()) {
                    System.out.println("    " + formatCar(car));
                }
            }

            System.out.println();
            System.out.println("Cars after " + MIN_YEAR + " with mileage less than " + MAX_MILEAGE + " km:");
            for (Department dept : orgFromFile.getDepartments()) {
                for (Car car : dept.getCars()) {
                    if (car.getYear() > MIN_YEAR && car.getMileage() < MAX_MILEAGE) {
                        System.out.println("  " + formatCar(car) + " - " + dept.getDepartmentName());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String formatCar(Car car) {
        return car.getCarId() + " " + car.getBrand() + " " + car.getModel()
                + ", " + car.getYear() + ", " + car.getPrice() + " rub, " + car.getMileage() + " km";
    }

    private static Organization createOrganization() {
        List<Car> centralCars = new ArrayList<>();
        centralCars.add(new Car("C01", "Toyota", "Camry", 2020, 85000, 42000));
        centralCars.add(new Car("C02", "BMW", "X5", 2016, 210000, 118000));
        centralCars.add(new Car("C03", "Kia", "Rio", 2022, 55000, 15000));
        Department central = new Department("D01", "CENTRAL SHOWROOM", "MINSK");
        central.setCars(centralCars);

        List<Car> eastCars = new ArrayList<>();
        eastCars.add(new Car("C04", "Volkswagen", "Polo", 2019, 45000, 67000));
        eastCars.add(new Car("C05", "Hyundai", "Tucson", 2021, 70000, 28000));
        eastCars.add(new Car("C06", "Lada", "Vesta", 2017, 60000, 45000));
        Department east = new Department("D02", "EAST SHOWROOM", "GOMEL");
        east.setCars(eastCars);

        List<Department> departments = new ArrayList<>();
        departments.add(central);
        departments.add(east);

        Organization org = new Organization("O01", "AutoMarket", "MINSK, Nezavisimosti Ave 1");
        org.setDepartments(departments);
        return org;
    }
}
