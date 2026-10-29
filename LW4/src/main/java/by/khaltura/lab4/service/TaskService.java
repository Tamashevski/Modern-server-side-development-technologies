package by.khaltura.lab4.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import by.khaltura.lab4.dao.CategoryDAO;
import by.khaltura.lab4.dao.TaskDAO;
import by.khaltura.lab4.model.AppUser;
import by.khaltura.lab4.model.Category;
import by.khaltura.lab4.model.Task;
import by.khaltura.lab4.model.TaskStatus;

// Правила работы с задачами: создание, модерация, отмена
public class TaskService {

    private final TaskDAO taskDAO = new TaskDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    private static void requireVerified(AppUser user) {
        if (!user.isVerified()) {
            throw new IllegalStateException("Сначала подтвердите личность: обратитесь к администратору");
        }
    }

    static void checkTransition(Task task, TaskStatus target) {
        if (!task.getStatus().canMoveTo(target)) {
            throw new IllegalStateException(String.format("Переход задачи из «%s» в «%s» недопустим",
                    task.getStatus().getTitle(), target.getTitle()));
        }
    }

    // Своя задача заказчика (чужие трогать нельзя)
    public Task getOwnTask(Integer taskId, AppUser customer) {
        Task task = taskDAO.findTaskById(taskId);
        if (task == null || !task.getCustomer().equals(customer)) {
            throw new IllegalArgumentException("Задача не найдена среди ваших");
        }
        return task;
    }

    // Новая задача всегда создаётся черновиком
    public Task create(AppUser customer, Integer categoryId, String title, String description,
                       BigDecimal budget, LocalDate deadline, String location) {
        requireVerified(customer);
        Category category = categoryDAO.findCategoryById(categoryId);
        if (category == null) throw new IllegalArgumentException("Категория не существует");
        if (title.length() < 5) throw new IllegalArgumentException("Заголовок – не короче 5 символов");
        if (description.length() < 10) throw new IllegalArgumentException("Описание – не короче 10 символов");
        if (budget.signum() <= 0) throw new IllegalArgumentException("Бюджет должен быть больше нуля");
        if (deadline.isBefore(LocalDate.now())) throw new IllegalArgumentException("Срок не может быть в прошлом");
        return taskDAO.addTask(new Task(customer, category, title, description, budget, deadline,
                location == null || location.isBlank() ? null : location));
    }

    // Черновик → на модерацию
    public Task submit(Integer taskId, AppUser customer) {
        requireVerified(customer);
        Task task = getOwnTask(taskId, customer);
        checkTransition(task, TaskStatus.PENDING_MODERATION);
        task.setStatus(TaskStatus.PENDING_MODERATION);
        task.setModerationComment(null);
        return taskDAO.updateTask(task);
    }

    // Решение администратора: опубликовать или вернуть в черновик с обязательной причиной
    public Task moderate(Integer taskId, boolean approve, String comment) {
        Task task = taskDAO.findTaskById(taskId);
        if (task == null) throw new IllegalArgumentException("Задача не найдена");
        if (task.getStatus() != TaskStatus.PENDING_MODERATION) {
            throw new IllegalStateException("Задача не находится на модерации");
        }
        if (!approve && (comment == null || comment.isBlank())) {
            throw new IllegalArgumentException("Укажите причину возврата");
        }
        TaskStatus target = approve ? TaskStatus.PUBLISHED : TaskStatus.DRAFT;
        checkTransition(task, target);
        task.setStatus(target);
        task.setModerationComment(approve ? null : comment);
        return taskDAO.updateTask(task);
    }

    // Отмена возможна, пока исполнитель не выбран
    public void cancel(Integer taskId, AppUser customer) {
        Task task = getOwnTask(taskId, customer);
        checkTransition(task, TaskStatus.CANCELLED);
        taskDAO.cancelTask(taskId);
    }

    // Работа выполнена: заказчик закрывает задачу
    public Task complete(Integer taskId, AppUser customer) {
        Task task = getOwnTask(taskId, customer);
        checkTransition(task, TaskStatus.COMPLETED);
        task.setStatus(TaskStatus.COMPLETED);
        return taskDAO.updateTask(task);
    }
}
