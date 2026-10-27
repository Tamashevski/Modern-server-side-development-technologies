package org.jdbc_lab.menus;

import org.jdbc_lab.prepared_statement.action.PreparedStatementAction;

import java.util.Scanner;
import java.util.logging.Logger;

public class PreparedStatementMenu {
    private final PreparedStatementAction action = new PreparedStatementAction();
    private static final Logger logger = Logger.getLogger(PreparedStatementMenu.class.getName());
    private final Scanner scanner = new Scanner(System.in);

    public void print() {
        while (true) {
            System.out.println("\n=== МЕНЮ PREPARED STATEMENT (Автосервис) ===");
            System.out.println("1. Добавить тестовые данные (Механики, Машины, Ремонты)");
            System.out.println("2. Показать ремонты конкретного механика");
            System.out.println("3. Обновить стоимость ремонта");
            System.out.println("4. Удалить старые ремонты");
            System.out.println("5. Просмотр всех таблиц");
            System.out.println("6. Показать машины, у которых есть ремонт дороже 150");
            System.out.print("Выберите пункт: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        action.addDefaultInfo();
                        System.out.println("Тестовые данные успешно добавлены!");
                        break;
                    case 2:
                        System.out.print("Введите ID механика (например, 1 или 2): ");
                        Long mechanicId = scanner.nextLong();
                        scanner.nextLine();
                        action.showRepairsByMechanic(mechanicId);
                        break;
                    case 3:
                        System.out.print("Введите ID ремонта для обновления: ");
                        Long jobId = scanner.nextLong();
                        scanner.nextLine();
                        System.out.print("Введите новую стоимость: ");
                        Integer newCost = scanner.nextInt();
                        scanner.nextLine();
                        action.updateRepairCost(jobId, newCost);
                        break;
                    case 4:
                        System.out.print("Введите дату (например, 2022-01-01), чтобы удалить ремонты до неё: ");
                        String date = scanner.nextLine();
                        action.deleteOldRepairs(date);
                        break;
                    case 5:
                        System.out.print("Просмотр всех таблиц");
                        action.showAll();
                        break;
                    case 6:
                        action.showCarsWithRepairCostGreaterThan(150);
                        break;
                    case 0:
                        return;
                    default:
                        System.out.println("Неверный пункт меню! Попробуйте снова.");
                }
            } catch (Exception e) {
                logger.severe("Ошибка при обработке меню PreparedStatement: " + e.getMessage());
                scanner.nextLine();
            }
        }
    }
}