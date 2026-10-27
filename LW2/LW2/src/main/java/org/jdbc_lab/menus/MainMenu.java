package org.jdbc_lab.menus;

import org.jdbc_lab.menus.PreparedStatementMenu;
import org.jdbc_lab.menus.StatementMenu;



import java.util.Scanner;

public class MainMenu {
    private final StatementMenu statementMenu = new StatementMenu();
    private final PreparedStatementMenu preparedStatementMenu = new PreparedStatementMenu();
    private final Scanner scanner = new Scanner(System.in);

    public void print() {
        while (true) {
            System.out.println("\n=== ГЛАВНОЕ МЕНЮ ===");
            System.out.println("1. Маркетплейс услуг (Statement)");
            System.out.println("2. Автослесари (PreparedStatement)");
            System.out.println("3. Выйти из программы");
            System.out.print("Выберите пункт: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // очищаем буфер

                switch (choice) {
                    case 1:
                        statementMenu.print();
                        break;
                    case 2:
                        preparedStatementMenu.print();
                        break;
                    case 3:
                        System.out.println("До свидания!");
                        return;
                    default:
                        System.out.println("Неверный пункт меню! Попробуйте снова.");
                }
            } catch (Exception e) {
                System.out.println("Ошибка ввода! Пожалуйста, введите число(1, 2 или 3.");
                scanner.nextLine();
            }
        }
    }
}

//-- Выбираем данные для вывода
//SELECT
//cl.name AS "Клиент",                              -- Имя клиента из таблицы clients
//cl.phone AS "Телефон",                            -- Телефон клиента
//cl.email AS "Email",                              -- Email клиента
//COUNT(o.id) AS "Количество заказов",              -- Считаем количество заказов (агрегатная функция)
//SUM(s.price) AS "Общая сумма (BYN)",              -- Суммируем стоимость всех услуг (агрегатная функция)
//e.first_name || ' ' || e.last_name AS "Исполнитель"  -- Склеиваем имя и фамилию исполнителя через пробел
//FROM clients cl                                       -- Берём таблицу клиентов с алиасом "cl"
//JOIN orders o ON cl.id = o.client_id              -- Связываем клиентов с заказами по client_id
//JOIN services s ON o.service_id = s.id            -- Связываем заказы с услугами по service_id
//JOIN executors e ON s.executor_id = e.id          -- Связываем услуги с исполнителями по executor_id
//WHERE                                                 -- Начинаем фильтрацию строк ДО группировки
//o.status = 'Завершён'                             -- Оставляем только завершённые заказы
//GROUP BY                                              -- Группируем результаты по полям
//cl.id, cl.name, cl.phone, cl.email,               -- Группировка по полям клиента
//e.id, e.first_name, e.last_name                   -- Группировка по полям исполнителя
//HAVING                                                -- Фильтрация групп ПОСЛЕ группировки
//SUM(s.price) > 200                                -- Оставляем только группы с общей суммой > 200 BYN
//ORDER BY                                              -- Сортируем результат
//    "Общая сумма (BYN)" DESC                          -- По убыванию общей суммы (от большей к меньшей)
//LIMIT 5;                                              -- Ограничиваем вывод первыми 5 строками