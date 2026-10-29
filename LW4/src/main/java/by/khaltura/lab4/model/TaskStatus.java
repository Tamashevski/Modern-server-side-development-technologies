package by.khaltura.lab4.model;

import java.util.Set;

// Состояния задачи и допустимые переходы между ними
public enum TaskStatus {
    DRAFT("Черновик"),
    PENDING_MODERATION("На модерации"),
    PUBLISHED("Опубликована"),
    IN_PROGRESS("В работе"),
    COMPLETED("Завершена"),
    CANCELLED("Отменена");

    private final String title;

    TaskStatus(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    // Куда можно перейти из текущего состояния
    public Set<TaskStatus> next() {
        return switch (this) {
            case DRAFT -> Set.of(PENDING_MODERATION, CANCELLED);
            case PENDING_MODERATION -> Set.of(PUBLISHED, DRAFT, CANCELLED);
            case PUBLISHED -> Set.of(IN_PROGRESS, CANCELLED);
            case IN_PROGRESS -> Set.of(COMPLETED);
            case COMPLETED, CANCELLED -> Set.of();
        };
    }

    public boolean canMoveTo(TaskStatus target) {
        return next().contains(target);
    }
}
