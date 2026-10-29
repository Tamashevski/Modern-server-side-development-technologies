package by.khaltura.lab4.menu;

import java.util.Scanner;

import by.khaltura.lab4.model.AppUser;
import by.khaltura.lab4.model.BidStatus;
import by.khaltura.lab4.model.ExecutorProfile;
import by.khaltura.lab4.model.Task;
import by.khaltura.lab4.model.TaskStatus;

public class ExecutorMenu extends Menu {

    public ExecutorMenu(Scanner in) {
        super(in);
    }

    @Override
    public void menu(AppUser authUser) {
        super.menu(authUser);
        while (true) {
            refreshUser();
            System.out.println();
            if (!authUser.isVerified()) {
                System.out.println("! Личность не подтверждена – откликаться на задачи нельзя");
            }
            System.out.println("0. Выход");
            System.out.println("1. Лента задач");
            System.out.println("2. Подробнее о задаче");
            System.out.println("3. Откликнуться на задачу");
            System.out.println("4. Мои отклики");
            System.out.println("5. Отозвать отклик");
            System.out.println("6. Мой профиль исполнителя");
            System.out.println("7. Заполнить / изменить профиль");
            System.out.println("8. Сменить пароль");
            try {
                switch (readLine("Ввод: ")) {
                    case "0" -> { return; }
                    case "1" -> printAll(taskDAO.getFeed(), "Открытых задач нет.");
                    case "2" -> showTask();
                    case "3" -> submitBid();
                    case "4" -> printAll(bidDAO.getBidsByExecutor(authUser.getUserId()).stream()
                            .map(b -> b + "\n\t  по задаче: " + b.getTask().getTitle()
                                    + " (id=" + b.getTask().getTaskId() + ")").toList(), "Откликов пока нет.");
                    case "5" -> {
                        bidService.cancel(readInt("ID отклика: "), authUser);
                        System.out.println("Отклик отозван.");
                    }
                    case "6" -> {
                        ExecutorProfile p = userDAO.getProfile(authUser.getUserId());
                        System.out.println(p == null ? "Профиль не заполнен." : p);
                    }
                    case "7" -> editProfile();
                    case "8" -> changePassword();
                    default -> System.out.println("Неверный ввод.");
                }
            } catch (RuntimeException ex) {
                System.out.println("Ошибка: " + ex.getMessage());
            }
        }
    }

    // Подробности задачи: исполнителю видно число откликов, но не чужие отклики
    private void showTask() {
        Task task = taskDAO.getTaskWithBids(readInt("ID задачи: "));
        if (task == null || task.getStatus() != TaskStatus.PUBLISHED) {
            throw new IllegalArgumentException("Опубликованная задача не найдена");
        }
        System.out.println(task);
        System.out.println("\tОписание: " + task.getDescription());
        System.out.println("\tЗаказчик: " + task.getCustomer().getFullName());
        long active = task.getBids().stream()
                .filter(b -> b.getStatus() != BidStatus.CANCELLED).count();
        System.out.println("\tОткликов: " + active);
        task.getBids().stream().filter(b -> b.getExecutor().equals(authUser))
                .forEach(b -> System.out.println("\tВаш отклик: " + b));
    }

    private void submitBid() {
        Integer taskId = readInt("ID задачи: ");
        var price = readMoney("Ваша цена, BYN: ");
        var deadline = readDate("Срок выполнения (ГГГГ-ММ-ДД): ");
        String text = readRequired("Текст предложения: ");
        System.out.println("Отклик отправлен: " + bidService.submit(taskId, authUser, price, deadline, text));
    }

    private void editProfile() {
        ExecutorProfile p = userDAO.getProfile(authUser.getUserId());
        if (p != null) System.out.println("Сейчас: " + p);
        String specialization = readRequired("Специализация: ");
        String skills = readLine("Навыки через запятую: ");
        String location = readLine("Город: ");
        var rate = readMoneyOptional("Ставка в час, BYN (можно пусто): ");
        System.out.println("Сохранено: " + userDAO.saveProfile(authUser.getUserId(), specialization,
                skills.isEmpty() ? null : skills, location.isEmpty() ? null : location, rate));
    }
}
