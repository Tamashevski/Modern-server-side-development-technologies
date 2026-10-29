package by.khaltura.lab4.menu;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import by.khaltura.lab4.dao.TaskDAO.CategoryStats;
import by.khaltura.lab4.model.AppUser;
import by.khaltura.lab4.model.Category;
import by.khaltura.lab4.model.Task;
import by.khaltura.lab4.model.TaskStatus;

public class AdminMenu extends Menu {

    public AdminMenu(Scanner in) {
        super(in);
    }

    @Override
    public void menu(AppUser authUser) {
        super.menu(authUser);
        while (true) {
            System.out.println();
            System.out.println("0. Выход");
            System.out.println("1. Все категории и их задачи (JOIN FETCH, решение n+1)");
            System.out.println("2. Найти задачу по ID вместе с откликами");
            System.out.println("3. Добавить категорию");
            System.out.println("4. Перенести задачу в другую категорию");
            System.out.println("5. Удалить задачу (отклики удаляются каскадно)");
            System.out.println("6. Удалить пустую категорию");
            System.out.println("7. Очередь модерации");
            System.out.println("8. Опубликовать задачу / вернуть на доработку");
            System.out.println("9. Список пользователей");
            System.out.println("10. Найти пользователя по email");
            System.out.println("11. Подтвердить личность пользователя");
            System.out.println("12. Запрос 1 (HQL): опубликованные задачи в диапазоне бюджета");
            System.out.println("13. Запрос 2 (Criteria): задачи по городу и статусу");
            System.out.println("14. Запрос 3 (HQL, GROUP BY): статистика бюджетов по категориям");
            try {
                switch (readLine("Ввод: ")) {
                    case "0" -> { return; }
                    case "1" -> seeAllCategories();
                    case "2" -> findTaskById();
                    case "3" -> addCategory();
                    case "4" -> moveTask();
                    case "5" -> deleteTask();
                    case "6" -> deleteCategory();
                    case "7" -> printAll(taskDAO.getTasksByStatus(TaskStatus.PENDING_MODERATION), "Очередь модерации пуста.");
                    case "8" -> moderate();
                    case "9" -> printAll(userDAO.getUsers(), "Пользователей нет.");
                    case "10" -> findUserByEmail();
                    case "11" -> verifyUser();
                    case "12" -> queryByBudget();
                    case "13" -> queryByCity();
                    case "14" -> queryStats();
                    default -> System.out.println("Неверный ввод.");
                }
            } catch (RuntimeException ex) {
                System.out.println("Ошибка: " + ex.getMessage());
            }
        }
    }

    private void seeAllCategories() {
        Set<Category> categories = categoryDAO.getCategoriesWithTasks();
        for (Category category : categories) {
            System.out.println(category);
            List<Task> tasks = category.getTasks();
            if (tasks.isEmpty()) {
                System.out.println("\t(задач нет)");
            }
            for (Task task : tasks) {
                System.out.println("\t" + task + " — заказчик: " + task.getCustomer().getFullName());
            }
        }
    }

    private void findTaskById() {
        Task task = taskDAO.getTaskWithBids(readInt("Введите ID задачи: "));
        if (task == null) throw new IllegalArgumentException("Задача не найдена");
        System.out.println(task);
        System.out.println("\tОписание: " + task.getDescription());
        System.out.println("\tЗаказчик: " + task.getCustomer());
        System.out.println("\tОтклики (" + task.getBids().size() + "):");
        task.getBids().forEach(b -> System.out.println("\t\t" + b));
    }

    private void addCategory() {
        printCategories();
        String name = readRequired("Название новой категории: ");
        String description = readLine("Описание (можно пусто): ");
        String parentInput = readLine("ID родительской категории (пусто – корневая): ");
        Category parent = null;
        if (!parentInput.isEmpty()) {
            parent = categoryDAO.findCategoryById(Integer.valueOf(parentInput));
            if (parent == null) throw new IllegalArgumentException("Родительская категория не найдена");
        }
        Category added = categoryDAO.addCategory(new Category(name, description.isEmpty() ? null : description, parent));
        System.out.println("Категория добавлена: " + added);
    }

    private void moveTask() {
        Integer taskId = readInt("Введите ID задачи: ");
        Task task = taskDAO.findTaskById(taskId);
        if (task == null) throw new IllegalArgumentException("Задача не найдена");
        System.out.println(task);
        printCategories();
        Category category = categoryDAO.findCategoryById(readInt("ID новой категории: "));
        if (category == null) throw new IllegalArgumentException("Категория не найдена");
        System.out.println("Задача перенесена: " + taskDAO.moveTaskToCategory(taskId, category));
    }

    private void deleteTask() {
        Integer taskId = readInt("Введите ID задачи: ");
        Task task = taskDAO.getTaskWithBids(taskId);
        if (task == null) throw new IllegalArgumentException("Задача не найдена");
        System.out.println(task + "\n\tОткликов будет удалено: " + task.getBids().size());
        if (!readLine("Удалить? (да/нет): ").equalsIgnoreCase("да")) {
            System.out.println("Отменено.");
            return;
        }
        taskDAO.deleteTask(taskId);
        System.out.println("Задача удалена.");
    }

    private void deleteCategory() {
        printCategories();
        categoryDAO.deleteCategory(readInt("ID категории: "));
        System.out.println("Категория удалена.");
    }

    private void moderate() {
        List<Task> queue = taskDAO.getTasksByStatus(TaskStatus.PENDING_MODERATION);
        printAll(queue, "Очередь модерации пуста.");
        if (queue.isEmpty()) return;
        Integer taskId = readInt("ID задачи: ");
        String decision = readLine("1 – опубликовать, 2 – вернуть на доработку: ");
        if (decision.equals("1")) {
            System.out.println("Опубликована: " + taskService.moderate(taskId, true, null));
        } else if (decision.equals("2")) {
            String comment = readRequired("Причина возврата: ");
            System.out.println("Возвращена: " + taskService.moderate(taskId, false, comment));
        } else {
            System.out.println("Неверный ввод.");
        }
    }

    private void findUserByEmail() {
        AppUser user = userDAO.getUserByEmail(readRequired("Введите email: "));
        if (user == null) throw new IllegalArgumentException("Пользователь не найден");
        System.out.println(user);
        if (user.getRole().isExecutor()) {
            var profile = userDAO.getProfile(user.getUserId());
            System.out.println("\t" + (profile == null ? "Профиль исполнителя не заполнен" : profile));
        }
    }

    private void verifyUser() {
        userDAO.getUsers().stream().filter(u -> !u.isVerified()).forEach(System.out::println);
        Integer id = readInt("ID пользователя: ");
        String decision = readLine("1 – подтвердить личность, 2 – снять подтверждение: ");
        if (!decision.equals("1") && !decision.equals("2")) throw new IllegalArgumentException("Неверный ввод");
        userDAO.setVerified(id, decision.equals("1"));
        System.out.println("Готово: " + userDAO.getUserById(id));
    }

    private void queryByBudget() {
        BigDecimal min = readMoney("Бюджет от: ");
        BigDecimal max = readMoney("Бюджет до: ");
        printCategories();
        String cat = readLine("ID категории (пусто – все; родительская включает дочерние): ");
        List<Task> tasks = taskDAO.findPublishedByBudget(min, max, cat.isEmpty() ? null : Integer.valueOf(cat));
        printAll(tasks, "Ничего не найдено.");
    }

    private void queryByCity() {
        String city = readRequired("Город (или часть адреса): ");
        System.out.println("Статусы: " + java.util.Arrays.toString(TaskStatus.values()));
        String s = readLine("Статус (пусто – любой): ").toUpperCase();
        TaskStatus status;
        try {
            status = s.isEmpty() ? null : TaskStatus.valueOf(s);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Нет такого статуса");
        }
        printAll(taskDAO.findByCityAndStatus(city, status), "Ничего не найдено.");
    }

    private void queryStats() {
        List<CategoryStats> stats = taskDAO.categoryStats();
        if (stats.isEmpty()) {
            System.out.println("Данных нет.");
            return;
        }
        System.out.printf("%-22s %6s %10s %10s %10s%n", "Категория", "Задач", "Средний", "Мин", "Макс");
        for (CategoryStats s : stats) {
            System.out.printf("%-22s %6d %10s %10s %10s%n",
                    s.category(), s.tasks(), s.avgBudget(), s.minBudget(), s.maxBudget());
        }
    }
}
