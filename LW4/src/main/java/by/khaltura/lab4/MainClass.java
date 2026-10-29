package by.khaltura.lab4;

import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import by.khaltura.lab4.config.SessionConfig;
import by.khaltura.lab4.dao.UserDAO;
import by.khaltura.lab4.menu.AdminMenu;
import by.khaltura.lab4.menu.CustomerMenu;
import by.khaltura.lab4.menu.ExecutorMenu;
import by.khaltura.lab4.menu.Menu;
import by.khaltura.lab4.model.AppUser;
import by.khaltura.lab4.model.Role;

public class MainClass {

    // Ссылка хранится в поле, чтобы настройка логгера не потерялась при сборке мусора
    private static final Logger HIBERNATE_LOG = Logger.getLogger("org.hibernate");

    public static void main(String[] args) {
        // Служебные сообщения Hibernate уровня INFO не выводим, только предупреждения и ошибки
        HIBERNATE_LOG.setLevel(Level.WARNING);

        // Кириллица при вводе: в консоли Windows своя кодировка, в IDE – UTF-8
        Charset inputCharset = System.console() != null ? System.console().charset() : Charset.defaultCharset();
        Scanner in = new Scanner(System.in, inputCharset);

        System.out.println("«Халтура» – ЛР4, Hibernate");
        System.out.println("Подключение к базе данных...");
        UserDAO userDAO = new UserDAO();

        try {
            while (true) {
                System.out.println("\n1. Вход\n2. Регистрация\n0. Выход");
                System.out.print("Ввод: ");
                if (!in.hasNextLine()) return;
                String choice = in.nextLine().trim();
                try {
                    switch (choice) {
                        case "0" -> { return; }
                        case "1" -> login(in, userDAO);
                        case "2" -> register(in, userDAO);
                        default -> System.out.println("Неверный ввод.");
                    }
                } catch (RuntimeException ex) {
                    System.out.println("Ошибка: " + ex.getMessage());
                }
            }
        } finally {
            SessionConfig.getInstance().shutdown();
        }
    }

    private static void login(Scanner in, UserDAO userDAO) {
        System.out.print("Email: ");
        String email = in.nextLine();
        System.out.print("Пароль: ");
        String password = in.nextLine();
        AppUser user = userDAO.authUser(email, password);
        if (user == null) {
            System.out.println("Неверный email или пароль");
            return;
        }
        Menu menu = switch (user.getRole()) {
            case ADMIN -> new AdminMenu(in);
            case CUSTOMER -> new CustomerMenu(in);
            case EXECUTOR -> new ExecutorMenu(in);
            // Совмещённая роль: пользователь выбирает, в каком качестве работать
            case CUSTOMER_EXECUTOR -> {
                System.out.print("Войти как: 1 – заказчик, 2 – исполнитель: ");
                yield in.nextLine().trim().equals("2") ? new ExecutorMenu(in) : new CustomerMenu(in);
            }
        };
        menu.menu(user);
    }

    private static void register(Scanner in, UserDAO userDAO) {
        System.out.print("Email: ");
        String email = in.nextLine().trim().toLowerCase();
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) throw new IllegalArgumentException("Некорректный email");
        if (userDAO.getUserByEmail(email) != null) throw new IllegalStateException("Пользователь с таким email уже есть");
        System.out.print("Пароль (не короче 8 символов): ");
        String password = in.nextLine();
        if (password.length() < 8) throw new IllegalArgumentException("Пароль короче 8 символов");
        System.out.print("ФИО: ");
        String fullName = in.nextLine().trim();
        if (fullName.length() < 3) throw new IllegalArgumentException("ФИО – не короче 3 символов");
        System.out.print("Телефон: ");
        String phone = in.nextLine().trim();
        if (!phone.matches("^\\+?[0-9\\s()-]{7,20}$")) throw new IllegalArgumentException("Некорректный телефон");
        System.out.print("Роль: 1 – заказчик, 2 – исполнитель, 3 – заказчик и исполнитель: ");
        Role role = switch (in.nextLine().trim()) {
            case "1" -> Role.CUSTOMER;
            case "2" -> Role.EXECUTOR;
            case "3" -> Role.CUSTOMER_EXECUTOR;
            default -> throw new IllegalArgumentException("Неверная роль");
        };
        AppUser user = userDAO.addUser(new AppUser(email, null, fullName, phone, role), password);
        System.out.println("Зарегистрирован: " + user);
        System.out.println("Чтобы публиковать задачи и откликаться, администратор должен подтвердить вашу личность.");
    }
}
