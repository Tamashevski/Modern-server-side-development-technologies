package org.jdbc_lab.statement.action;

import org.jdbc_lab.statement.dao.*;
import org.jdbc_lab.statement.model.*;

import java.util.List;
import java.util.Scanner;

public class StatementAction {
    private final ExecutorDao executorDao = new ExecutorDao();
    private final ServiceDao serviceDao = new ServiceDao();
    private final ClientDao clientDao = new ClientDao();
    private final OrderDao orderDao = new OrderDao();

    public void deleteAllInfo() {
        executorDao.clearTable();
        serviceDao.clearTable();
        clientDao.clearTable();
        orderDao.clearTable();
    }

    public void addDefaultInfo() {
        if (executorDao.getCount() == 0) {
            executorDao.insert(new Executor("Иван", "Петров", "Сантехник", 5));
            executorDao.insert(new Executor("Анна", "Сидорова", "Электрик", 4));
            executorDao.insert(new Executor("Сергей", "Иванов", "Плотник", 5));
            executorDao.insert(new Executor("Михаил", "Козлов", "Маляр", 3));
            executorDao.insert(new Executor("Екатерина", "Новикова", "Дизайнер", 5));
            executorDao.insert(new Executor("Александр", "Морозов", "Электрик", 4));
            executorDao.insert(new Executor("Ольга", "Волкова", "Сантехник", 4));
            executorDao.insert(new Executor("Дмитрий", "Соколов", "Плотник", 3));
            executorDao.insert(new Executor("Наталья", "Лебедева", "Уборка", 5));
            executorDao.insert(new Executor("Андрей", "Кузнецов", "Сварщик", 4));
        }

        if (clientDao.getCount() == 0) {
            clientDao.insert(new Client("Мария", "+375291111111", "maria@mail.ru"));
            clientDao.insert(new Client("Дмитрий", "+375292222222", "dmitry@mail.ru"));
            clientDao.insert(new Client("Елена", "+375293333333", "elena@mail.ru"));
            clientDao.insert(new Client("Алексей", "+375294444444", "alexey@mail.ru"));
            clientDao.insert(new Client("Татьяна", "+375295555555", "tatiana@mail.ru"));
            clientDao.insert(new Client("Виктор", "+375296666666", "victor@mail.ru"));
            clientDao.insert(new Client("Светлана", "+375297777777", "svetlana@mail.ru"));
            clientDao.insert(new Client("Павел", "+375298888888", "pavel@mail.ru"));
            clientDao.insert(new Client("Ирина", "+375299999999", "irina@mail.ru"));
            clientDao.insert(new Client("Николай", "+375331111111", "nikolay@mail.ru"));
        }

        if (serviceDao.getCount() == 0) {
            // Услуги для исполнителя 1 (Сантехник)
            serviceDao.insert(new Service("Ремонт труб", 1L, 100, "Сантехника"));
            serviceDao.insert(new Service("Установка смесителя", 1L, 80, "Сантехника"));
            serviceDao.insert(new Service("Прочистка канализации", 1L, 120, "Сантехника"));

            // Услуги для исполнителя 2 (Электрик)
            serviceDao.insert(new Service("Замена розетки", 2L, 50, "Электрика"));
            serviceDao.insert(new Service("Монтаж люстры", 2L, 90, "Электрика"));
            serviceDao.insert(new Service("Прокладка кабеля", 2L, 150, "Электрика"));

            // Услуги для исполнителя 3 (Плотник)
            serviceDao.insert(new Service("Сборка мебели", 3L, 150, "Плотницкие работы"));
            serviceDao.insert(new Service("Установка двери", 3L, 200, "Плотницкие работы"));
            serviceDao.insert(new Service("Ремонт пола", 3L, 250, "Плотницкие работы"));

            // Услуги для исполнителя 4 (Маляр)
            serviceDao.insert(new Service("Покраска стен", 4L, 180, "Малярные работы"));
            serviceDao.insert(new Service("Поклейка обоев", 4L, 160, "Малярные работы"));

            // Услуги для исполнителя 5 (Дизайнер)
            serviceDao.insert(new Service("Дизайн-проект", 5L, 500, "Дизайн"));
            serviceDao.insert(new Service("Консультация", 5L, 100, "Дизайн"));

            // Услуги для исполнителя 6 (Электрик)
            serviceDao.insert(new Service("Установка счетчика", 6L, 120, "Электрика"));
            serviceDao.insert(new Service("Ремонт проводки", 6L, 200, "Электрика"));

            // Услуги для исполнителя 7 (Сантехник)
            serviceDao.insert(new Service("Установка унитаза", 7L, 150, "Сантехника"));
            serviceDao.insert(new Service("Ремонт бачка", 7L, 70, "Сантехника"));

            // Услуги для исполнителя 8 (Плотник)
            serviceDao.insert(new Service("Изготовление полок", 8L, 130, "Плотницкие работы"));
            serviceDao.insert(new Service("Ремонт окна", 8L, 180, "Плотницкие работы"));

            // Услуги для исполнителя 9 (Уборка)
            serviceDao.insert(new Service("Генеральная уборка", 9L, 200, "Уборка"));
            serviceDao.insert(new Service("Химчистка мебели", 9L, 150, "Уборка"));

            // Услуги для исполнителя 10 (Сварщик)
            serviceDao.insert(new Service("Сварка труб", 10L, 250, "Сварочные работы"));
            serviceDao.insert(new Service("Изготовление ворот", 10L, 400, "Сварочные работы"));
        }

        if (orderDao.getCount() == 0) {
            // Заказы за сентябрь 2026
            orderDao.insert(new Order(1L, 1L, "2026-09-01", "Завершён"));
            orderDao.insert(new Order(2L, 2L, "2026-09-05", "Завершён"));
            orderDao.insert(new Order(3L, 3L, "2026-09-10", "Завершён"));
            orderDao.insert(new Order(4L, 4L, "2026-09-12", "Завершён"));
            orderDao.insert(new Order(5L, 5L, "2026-09-15", "Завершён"));

            // Заказы за конец сентября
            orderDao.insert(new Order(6L, 6L, "2026-09-20", "Завершён"));
            orderDao.insert(new Order(7L, 7L, "2026-09-22", "Завершён"));
            orderDao.insert(new Order(8L, 8L, "2026-09-25", "В работе"));
            orderDao.insert(new Order(9L, 9L, "2026-09-28", "В работе"));
            orderDao.insert(new Order(10L, 10L, "2026-09-30", "Новый"));

            // Заказы за октябрь 2026
            orderDao.insert(new Order(11L, 1L, "2026-10-01", "Новый"));
            orderDao.insert(new Order(12L, 2L, "2026-10-01", "В работе"));
            orderDao.insert(new Order(13L, 3L, "2026-10-02", "Новый"));
            orderDao.insert(new Order(14L, 4L, "2026-10-02", "Завершён"));
            orderDao.insert(new Order(15L, 5L, "2026-10-03", "Новый"));
            orderDao.insert(new Order(16L, 6L, "2026-10-03", "В работе"));
            orderDao.insert(new Order(17L, 7L, "2026-10-04", "Новый"));
            orderDao.insert(new Order(18L, 8L, "2026-10-04", "Завершён"));
            orderDao.insert(new Order(19L, 9L, "2026-10-05", "В работе"));
            orderDao.insert(new Order(20L, 10L, "2026-10-05", "Новый"));
        }
    }

    public void getAllInfo() {
        System.out.println("\n=== ИСПОЛНИТЕЛИ ===");
        List<Executor> executors = executorDao.getAll();
        for (Executor executor : executors) {
            System.out.println(executor);
        }

        System.out.println("\n=== КЛИЕНТЫ ===");
        List<Client> clients = clientDao.getAll();
        for (Client client : clients) {
            System.out.println(client);
        }

        System.out.println("\n=== УСЛУГИ ===");
        List<Service> services = serviceDao.getAll();
        for (Service service : services) {
            System.out.println(service);
        }

        System.out.println("\n=== ЗАКАЗЫ ===");
        List<Order> orders = orderDao.getAll();
        for (Order order : orders) {
            System.out.println(order);
        }
    }

    public void getExecutorsSortedByName() {
        System.out.println("\n=== ИСПОЛНИТЕЛИ (отсортированные по фамилии и имени) ===");
        List<Executor> executors = executorDao.getAllSortedBySurname();
        for (Executor executor : executors) {
            System.out.println(executor);
        }
    }

    public void getServicesByPriceGreaterThan(Scanner scanner) {
        System.out.print("Введите цену, выше которой нужно найти услуги: ");
        int price = scanner.nextInt();
        scanner.nextLine();

        System.out.println("\n=== УСЛУГИ (цена выше " + price + ", отсортированы по цене) ===");
        List<Service> services = serviceDao.getByPriceGreaterThan(price);
        for (Service service : services) {
            System.out.println(service);
        }
    }

    public void updateRandomOrderStatus(Scanner scanner) {
        System.out.print("Введите ID клиента: ");
        long clientId = scanner.nextLong();
        scanner.nextLine();

        System.out.print("Введите новый статус заказа: ");
        String newStatus = scanner.nextLine();

        orderDao.updateRandomOrderStatus(clientId, newStatus);
        System.out.println("Статус случайного заказа успешно обновлён!");
    }

    public void getServicesByCategoryStartingWith(Scanner scanner) {
        System.out.print("Введите первую букву специализации (категории): ");
        String letter = scanner.nextLine();

        System.out.println("\n=== УСЛУГИ (категория начинается на '" + letter + "') ===");
        List<Service> services = serviceDao.getByCategoryStartingWith(letter);
        for (Service service : services) {
            System.out.println(service);
        }
    }

    public void updateClientPhoneByOrderStatus(Scanner scanner) {
        System.out.print("Введите новый телефон: ");
        String newPhone = scanner.nextLine();

        System.out.print("Введите статус заказов (например, 'В работе'): ");
        String status = scanner.nextLine();

        clientDao.updateClientPhoneByOrderStatus(newPhone, status);
        System.out.println("Телефоны клиентов успешно обновлены!");
    }

    public void getServicesCountByCategory() {
        System.out.println("\n=== КОЛИЧЕСТВО УСЛУГ ПО КАТЕГОРИЯМ ===");
        List<String> result = serviceDao.getServicesCountByCategory();
        for (String line : result) {
            System.out.println(line);
        }
    }

}