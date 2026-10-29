package by.khaltura.lab4.menu;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.Scanner;

import by.khaltura.lab4.dao.BidDAO;
import by.khaltura.lab4.dao.CategoryDAO;
import by.khaltura.lab4.dao.TaskDAO;
import by.khaltura.lab4.dao.UserDAO;
import by.khaltura.lab4.model.AppUser;
import by.khaltura.lab4.model.Category;
import by.khaltura.lab4.service.BidService;
import by.khaltura.lab4.service.TaskService;

// Общая часть всех меню: DAO, сервисы и чтение ввода с проверкой формата
public abstract class Menu {

    protected static final UserDAO userDAO = new UserDAO();
    protected static final CategoryDAO categoryDAO = new CategoryDAO();
    protected static final TaskDAO taskDAO = new TaskDAO();
    protected static final BidDAO bidDAO = new BidDAO();
    protected static final TaskService taskService = new TaskService();
    protected static final BidService bidService = new BidService();

    protected final Scanner in;
    protected AppUser authUser;

    protected Menu(Scanner in) {
        this.in = in;
    }

    public void menu(AppUser authUser) {
        this.authUser = authUser;
        System.out.printf("%nЗдравствуйте, %s. Ваша роль: %s%n", authUser.getFullName(), authUser.getRole().getTitle());
    }

    // Пользователь мог измениться (например, админ подтвердил личность) – перечитываем из БД
    protected void refreshUser() {
        authUser = userDAO.getUserById(authUser.getUserId());
    }

    protected String readLine(String prompt) {
        System.out.print(prompt);
        return in.nextLine().trim();
    }

    protected String readRequired(String prompt) {
        String s = readLine(prompt);
        if (s.isEmpty()) throw new IllegalArgumentException("Поле не может быть пустым");
        return s;
    }

    protected Integer readInt(String prompt) {
        try {
            return Integer.valueOf(readLine(prompt));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Нужно ввести целое число");
        }
    }

    protected BigDecimal readMoney(String prompt) {
        try {
            return new BigDecimal(readLine(prompt).replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Нужно ввести сумму, например 120.50");
        }
    }

    // Пустой ввод – null (для необязательных полей)
    protected BigDecimal readMoneyOptional(String prompt) {
        String s = readLine(prompt);
        if (s.isEmpty()) return null;
        try {
            return new BigDecimal(s.replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Нужно ввести сумму, например 120.50");
        }
    }

    protected LocalDate readDate(String prompt) {
        try {
            return LocalDate.parse(readLine(prompt));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Дата в формате ГГГГ-ММ-ДД, например " + LocalDate.now().plusDays(7));
        }
    }

    protected static void printAll(Collection<?> items, String emptyText) {
        if (items.isEmpty()) {
            System.out.println(emptyText);
            return;
        }
        items.forEach(System.out::println);
    }

    protected void changePassword() {
        String password = readRequired("Новый пароль (не короче 8 символов): ");
        if (password.length() < 8) throw new IllegalArgumentException("Пароль короче 8 символов");
        userDAO.updatePassword(authUser.getUserId(), password);
        System.out.println("Пароль изменён.");
    }

    // Дерево категорий: дочерние выводятся с отступом под родительской
    protected void printCategories() {
        var all = categoryDAO.getCategories();
        for (Category c : all) {
            if (c.getParent() == null) {
                System.out.println(c);
                for (Category child : all) {
                    if (child.getParent() != null && child.getParent().equals(c)) {
                        System.out.println("\t└ " + child);
                    }
                }
            }
        }
    }
}
