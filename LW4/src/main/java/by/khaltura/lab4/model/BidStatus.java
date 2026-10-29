package by.khaltura.lab4.model;

public enum BidStatus {
    PENDING("Ожидает решения"),
    ACCEPTED("Выбран"),
    REJECTED("Отклонён"),
    CANCELLED("Отозван");

    private final String title;

    BidStatus(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
