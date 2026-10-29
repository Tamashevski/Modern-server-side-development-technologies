package by.khaltura.lab4.model;

// Роли пользователей. CUSTOMER_EXECUTOR совмещает права заказчика и исполнителя
public enum Role {
    CUSTOMER("Заказчик"),
    EXECUTOR("Исполнитель"),
    CUSTOMER_EXECUTOR("Заказчик и исполнитель"),
    ADMIN("Администратор");

    private final String title;

    Role(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public boolean isCustomer() {
        return this == CUSTOMER || this == CUSTOMER_EXECUTOR;
    }

    public boolean isExecutor() {
        return this == EXECUTOR || this == CUSTOMER_EXECUTOR;
    }
}
