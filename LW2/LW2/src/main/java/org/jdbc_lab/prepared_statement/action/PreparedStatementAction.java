package org.jdbc_lab.prepared_statement.action;

import org.jdbc_lab.prepared_statement.dao.CarDao;
import org.jdbc_lab.prepared_statement.dao.MechanicDao;
import org.jdbc_lab.prepared_statement.dao.RepairJobDao;
import org.jdbc_lab.prepared_statement.model.Car;
import org.jdbc_lab.prepared_statement.model.Mechanic;
import org.jdbc_lab.prepared_statement.model.RepairJob;

import java.util.List;

public class PreparedStatementAction {
    private final MechanicDao mechanicDao = new MechanicDao();
    private final CarDao carDao = new CarDao();
    private final RepairJobDao repairJobDao = new RepairJobDao();

    public void addDefaultInfo() {

        if (mechanicDao.getCount() == 0) {
            mechanicDao.insert(new Mechanic(null, "Алексей", "Смирнов", 5, "Двигатель", "+375294444444"));
            mechanicDao.insert(new Mechanic(null, "Борис", "Кузнецов", 3, "Ходовая", "+375295555555"));
            mechanicDao.insert(new Mechanic(null, "Виктор", "Попов", 7, "Трансмиссия", "+375296666666"));
            mechanicDao.insert(new Mechanic(null, "Григорий", "Васильев", 4, "Электрика", "+375297777777"));
            mechanicDao.insert(new Mechanic(null, "Дмитрий", "Зайцев", 8, "Двигатель", "+375298888888"));
            mechanicDao.insert(new Mechanic(null, "Евгений", "Павлов", 2, "Кузов", "+375299999999"));
            mechanicDao.insert(new Mechanic(null, "Жанна", "Семенова", 6, "Диагностика", "+375331111111"));
            mechanicDao.insert(new Mechanic(null, "Захар", "Голубев", 5, "Ходовая", "+375332222222"));
            mechanicDao.insert(new Mechanic(null, "Илья", "Виноградов", 9, "Двигатель", "+375333333333"));
            mechanicDao.insert(new Mechanic(null, "Ксения", "Богданова", 4, "Электрика", "+375334444444"));
        }

        if (carDao.getCount() == 0) {
            carDao.insert(new Car(null, "Toyota", "Camry", 2018, "А123БВ"));
            carDao.insert(new Car(null, "BMW", "X5", 2020, "Е456КМ"));
            carDao.insert(new Car(null, "Lada", "Vesta", 2015, "О789ОР"));
            carDao.insert(new Car(null, "Mercedes", "E-Class", 2019, "М555ММ"));
            carDao.insert(new Car(null, "Audi", "A6", 2021, "А777АА"));
            carDao.insert(new Car(null, "Volkswagen", "Tiguan", 2017, "Т888ТТ"));
            carDao.insert(new Car(null, "Hyundai", "Tucson", 2022, "Н999НН"));
            carDao.insert(new Car(null, "Kia", "Sportage", 2020, "К111КК"));
            carDao.insert(new Car(null, "Nissan", "Qashqai", 2016, "Н222НН"));
            carDao.insert(new Car(null, "Renault", "Duster", 2019, "Р333РР"));
        }

        if (repairJobDao.getCount() == 0) {
            List<Mechanic> mechanics = mechanicDao.getAll();
            List<Car> cars = carDao.getAll();

            if (mechanics.isEmpty() || cars.isEmpty()) {
                System.out.println("Не удалось добавить ремонты: сначала заполните таблицы механиков и машин.");
                return;
            }

            addRepairJobs(mechanics, cars);
        }
    }

    private void addRepairJobs(List<Mechanic> mechanics, List<Car> cars) {
        int mechanicCount = mechanics.size();
        int carCount = cars.size();

        addRepairJob(mechanics, cars, 1, 1, "2021-01-10", 80);
        addRepairJob(mechanics, cars, 2, 2, "2022-05-15", 300);
        addRepairJob(mechanics, cars, 1, 3, "2023-10-01", 150);

        // Работы механика 1 (Алексей, Двигатель)
        addRepairJob(mechanics, cars, 1, 1, "2024-03-12", 200);
        addRepairJob(mechanics, cars, 1, 4, "2024-07-20", 350);
        addRepairJob(mechanics, cars, 1, 5, "2025-01-15", 180);
        addRepairJob(mechanics, cars, 1, 2, "2026-02-10", 220);

        // Работы механика 2 (Борис, Ходовая)
        addRepairJob(mechanics, cars, 2, 3, "2024-04-18", 120);
        addRepairJob(mechanics, cars, 2, 6, "2024-11-25", 280);
        addRepairJob(mechanics, cars, 2, 1, "2025-06-30", 160);

        // Работы механика 3 (Виктор, Трансмиссия)
        addRepairJob(mechanics, cars, 3, 2, "2024-05-05", 400);
        addRepairJob(mechanics, cars, 3, 7, "2025-08-14", 450);
        addRepairJob(mechanics, cars, 3, 4, "2026-01-22", 380);

        // Работы механика 4 (Григорий, Электрика)
        addRepairJob(mechanics, cars, 4, 5, "2024-06-10", 90);
        addRepairJob(mechanics, cars, 4, 8, "2025-03-18", 110);
        addRepairJob(mechanics, cars, 4, 3, "2026-04-05", 95);

        // Работы механика 5 (Дмитрий, Двигатель)
        addRepairJob(mechanics, cars, 5, 6, "2024-09-22", 500);
        addRepairJob(mechanics, cars, 5, 9, "2025-12-01", 600);
        addRepairJob(mechanics, cars, 5, 1, "2026-05-15", 480);

        // Работы механика 6 (Евгений, Кузов)
        addRepairJob(mechanics, cars, 6, 10, "2025-02-14", 250);
        addRepairJob(mechanics, cars, 6, 7, "2026-06-20", 300);

        // Работы механика 7 (Жанна, Диагностика)
        addRepairJob(mechanics, cars, 7, 8, "2025-04-10", 70);
        addRepairJob(mechanics, cars, 7, 9, "2025-10-25", 85);
        addRepairJob(mechanics, cars, 7, 10, "2026-07-08", 75);

        // Работы механика 8 (Захар, Ходовая)
        addRepairJob(mechanics, cars, 8, 4, "2025-05-17", 190);
        addRepairJob(mechanics, cars, 8, 2, "2026-08-12", 210);

        // Работы механика 9 (Илья, Двигатель) - опытный, дорогие работы
        addRepairJob(mechanics, cars, 9, 5, "2025-07-22", 700);
        addRepairJob(mechanics, cars, 9, 6, "2026-03-14", 850);
        addRepairJob(mechanics, cars, 9, 7, "2026-09-01", 900);

        // Работы механика 10 (Ксения, Электрика)
        addRepairJob(mechanics, cars, 10, 9, "2025-11-05", 130);
        addRepairJob(mechanics, cars, 10, 10, "2026-09-28", 140);

        System.out.println("Добавлено ремонтов: " + (mechanicCount * 3) + " (механиков: " + mechanicCount + ", машин: " + carCount + ")");
    }

    private void addRepairJob(List<Mechanic> mechanics, List<Car> cars, int mechanicNumber, int carNumber, String date, int cost) {
        if (mechanicNumber > mechanics.size() || carNumber > cars.size()) {
            System.out.println("Пропущен ремонт для механика №" + mechanicNumber + " и машины №" + carNumber + ": не хватает данных в таблицах.");
            return;
        }

        Long mechanicId = mechanics.get(mechanicNumber - 1).getId();
        Long carId = cars.get(carNumber - 1).getId();

        repairJobDao.insert(new RepairJob(null, carId, mechanicId, date, cost));
    }

    public void showAll() {
        System.out.println("\nМеханики");
        List<Mechanic> mechanics = mechanicDao.getAll();
        if (mechanics.isEmpty()) {
            System.out.println("Таблица пуста.");
        } else {
            for (Mechanic mechanic : mechanics) {
                System.out.println(mechanic);
            }
        }

        System.out.println("\nМашины");
        List<Car> cars = carDao.getAll();
        if (cars.isEmpty()) {
            System.out.println("Таблица пуста.");
        } else {
            for (Car car : cars) {
                System.out.println(car);
            }
        }

        System.out.println("\nРемонты");
        List<RepairJob> jobs = repairJobDao.getAll();
        if (jobs.isEmpty()) {
            System.out.println("Таблица пуста.");
        } else {
            for (RepairJob job : jobs) {
                System.out.println(job);
            }
        }
    }

    public void showRepairsByMechanic(Long mechanicId) {
        System.out.println("\n=== РЕМОНТЫ МЕХАНИКА С ID = " + mechanicId + " ===");
        List<RepairJob> jobs = repairJobDao.getByMechanicId(mechanicId);

        if (jobs.isEmpty()) {
            System.out.println("У этого механика пока нет записей о ремонтах.");
        } else {
            for (RepairJob job : jobs) {
                System.out.println(job);
            }
        }
    }

public void showCarsWithRepairCostGreaterThan(Integer cost) {
        System.out.println("\nМашины у которых ремонт дороже " + cost);
        List<Car> cars = carDao.getByRepairCostGreaterThan(cost);

        if (cars.isEmpty()) {
            System.out.println("Таких машин нет.");
        } else {
            for (Car car : cars) {
                System.out.println(car);
            }
        }
    }

    public void updateRepairCost(Long jobId, Integer newCost) {
        repairJobDao.updateCost(jobId, newCost);
        System.out.println("Стоимость ремонта с ID=" + jobId + " успешно обновлена на " + newCost);
    }

    public void deleteOldRepairs(String date) {
        int count = repairJobDao.deleteOlderThan(date);
        System.out.println("Удалено старых ремонтов (до даты " + date + "): " + count + " шт.");
    }
}