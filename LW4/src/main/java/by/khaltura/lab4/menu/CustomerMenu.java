package by.khaltura.lab4.menu;

import java.util.List;
import java.util.Scanner;

import by.khaltura.lab4.model.AppUser;
import by.khaltura.lab4.model.Bid;
import by.khaltura.lab4.model.BidStatus;
import by.khaltura.lab4.model.Task;

public class CustomerMenu extends Menu {

    public CustomerMenu(Scanner in) {
        super(in);
    }

    @Override
    public void menu(AppUser authUser) {
        super.menu(authUser);
        while (true) {
            refreshUser();
            System.out.println();
            if (!authUser.isVerified()) {
                System.out.println("! Личность не подтверждена – создавать и публиковать задачи нельзя");
            }
            System.out.println("0. Выход");
            System.out.println("1. Мои задачи");
            System.out.println("2. Создать задачу (черновик)");
            System.out.println("3. Отправить черновик на модерацию");
            System.out.println("4. Отклики на мою задачу");
            System.out.println("5. Выбрать исполнителя");
            System.out.println("6. Отметить задачу выполненной");
            System.out.println("7. Отменить задачу");
            System.out.println("8. Сменить пароль");
            try {
                switch (readLine("Ввод: ")) {
                    case "0" -> { return; }
                    case "1" -> printAll(taskDAO.getTasksByCustomer(authUser.getUserId()), "У вас пока нет задач.");
                    case "2" -> createTask();
                    case "3" -> System.out.println("Отправлена: "
                            + taskService.submit(readInt("ID черновика: "), authUser));
                    case "4" -> showBids();
                    case "5" -> selectExecutor();
                    case "6" -> System.out.println("Завершена: "
                            + taskService.complete(readInt("ID задачи: "), authUser));
                    case "7" -> cancelTask();
                    case "8" -> changePassword();
                    default -> System.out.println("Неверный ввод.");
                }
            } catch (RuntimeException ex) {
                System.out.println("Ошибка: " + ex.getMessage());
            }
        }
    }

    private void createTask() {
        printCategories();
        Integer categoryId = readInt("ID категории: ");
        String title = readRequired("Заголовок: ");
        String description = readRequired("Описание: ");
        var budget = readMoney("Бюджет, BYN: ");
        var deadline = readDate("Срок (ГГГГ-ММ-ДД): ");
        String location = readLine("Адрес (можно пусто): ");
        Task task = taskService.create(authUser, categoryId, title, description, budget, deadline, location);
        System.out.println("Черновик создан: " + task);
        if (readLine("Сразу отправить на модерацию? (да/нет): ").equalsIgnoreCase("да")) {
            System.out.println("Отправлена: " + taskService.submit(task.getTaskId(), authUser));
        }
    }

    // Отклики на свою задачу. Ленивый список bids инициализируется в TaskDAO.getTaskWithBids
    private void showBids() {
        Integer taskId = readInt("ID задачи: ");
        taskService.getOwnTask(taskId, authUser);
        Task task = taskDAO.getTaskWithBids(taskId);
        System.out.println(task);
        List<Bid> bids = task.getBids().stream().filter(b -> b.getStatus() != BidStatus.CANCELLED).toList();
        printAll(bids, "Откликов пока нет.");
    }

    private void selectExecutor() {
        Integer taskId = readInt("ID задачи: ");
        taskService.getOwnTask(taskId, authUser);
        Task task = taskDAO.getTaskWithBids(taskId);
        List<Bid> pending = task.getBids().stream().filter(b -> b.getStatus() == BidStatus.PENDING).toList();
        printAll(pending, "Нет откликов, ожидающих решения.");
        if (pending.isEmpty()) return;
        Bid chosen = bidService.select(readInt("ID выбранного отклика: "), authUser);
        System.out.println("Исполнитель выбран: " + chosen.getExecutor().getFullName()
                + ", телефон " + chosen.getExecutor().getPhone() + ". Остальные отклики отклонены.");
    }

    private void cancelTask() {
        Integer taskId = readInt("ID задачи: ");
        System.out.println(taskService.getOwnTask(taskId, authUser));
        if (readLine("Отменить задачу? (да/нет): ").equalsIgnoreCase("да")) {
            taskService.cancel(taskId, authUser);
            System.out.println("Задача отменена, ожидающие отклики отклонены.");
        }
    }
}
