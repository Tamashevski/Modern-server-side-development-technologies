package org.jdbc_lab.menus;

import org.jdbc_lab.statement.action.StatementAction;

import java.util.Scanner;
import java.util.logging.Logger;

public class StatementMenu {
    private final StatementAction statementAction = new StatementAction();
    private static final Logger logger = Logger.getLogger(StatementMenu.class.getName());
    private final Scanner scanner = new Scanner(System.in);

    public void print() {
        while (true) {
            System.out.println("\n=== МЕНЮ STATEMENT (Маркетплейс услуг) ===");
            System.out.println("1. Добавить информацию по умолчанию");
            System.out.println("2. Просмотреть всю информацию");
            System.out.println("3. Очистить таблицы");
            System.out.println("4. Показать исполнителей (сортировка по фамилии и имени)");
            System.out.println("5. Найти услуги дороже заданной цены");
            System.out.println("6. Обновить статус случайного заказа клиента");
            System.out.println("7. Найти услуги по первой букве категории");
            System.out.println("8. Обновить телефон клиентов по статусу заказа");
            System.out.println("9. Показать количество услуг по категориям");
            System.out.println("0. Выйти в главное меню");
            System.out.print("Выберите пункт: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        statementAction.addDefaultInfo();
                        System.out.println("Информация успешно добавлена!");
                        break;
                    case 2:
                        statementAction.getAllInfo();
                        break;
                    case 3:
                        statementAction.deleteAllInfo();
                        System.out.println("Таблицы успешно очищены!");
                        break;
                    case 4:
                        statementAction.getExecutorsSortedByName();
                        break;
                    case 5:
                        statementAction.getServicesByPriceGreaterThan(scanner);
                        break;
                    case 6:
                        statementAction.updateRandomOrderStatus(scanner);
                        break;
                    case 7:
                        statementAction.getServicesByCategoryStartingWith(scanner);
                        break;
                    case 8:
                        statementAction.updateClientPhoneByOrderStatus(scanner);
                        break;
                    case 9:
                        statementAction.getServicesCountByCategory();
                        break;
                    case 0:
                        return;
                    default:
                        System.out.println("Неверный пункт меню! Попробуйте снова.");
                }
            } catch (Exception e) {
                logger.severe("Ошибка при обработке меню Statement: " + e.getMessage());
                scanner.nextLine();
            }
        }
    }
}