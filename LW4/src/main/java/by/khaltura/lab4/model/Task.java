package by.khaltura.lab4.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "task")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Integer taskId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private AppUser customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "budget", nullable = false, precision = 10, scale = 2)
    private BigDecimal budget;

    @Column(name = "deadline", nullable = false)
    private LocalDate deadline;

    @Column(name = "location")
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TaskStatus status = TaskStatus.DRAFT;

    @Column(name = "moderation_comment", columnDefinition = "TEXT")
    private String moderationComment;

    @Column(name = "created_at", nullable = false,
            insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL,
               orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("proposedPrice ASC")
    private List<Bid> bids = new ArrayList<>();

    public Task() {
    }

    public Task(AppUser customer, Category category, String title, String description,
                BigDecimal budget, LocalDate deadline, String location) {
        this.customer = customer;
        this.category = category;
        this.title = title;
        this.description = description;
        this.budget = budget;
        this.deadline = deadline;
        this.location = location;
    }

    public Integer getTaskId() { return taskId; }
    public AppUser getCustomer() { return customer; }
    public void setCustomer(AppUser customer) { this.customer = customer; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getBudget() { return budget; }
    public void setBudget(BigDecimal budget) { this.budget = budget; }
    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public String getModerationComment() { return moderationComment; }
    public void setModerationComment(String moderationComment) { this.moderationComment = moderationComment; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<Bid> getBids() { return bids; }

    @Override
    public String toString() {
        return String.format("Задача [id=%d, «%s», %s, бюджет %s BYN, до %s, %s, %s]",
                taskId, title, category.getName(), budget, deadline,
                location == null ? "без адреса" : location, status.getTitle())
                + (moderationComment != null && status == TaskStatus.DRAFT
                    ? "\n\t  Возвращена модератором: " + moderationComment : "");
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task other)) return false;
        return taskId != null && taskId.equals(other.taskId);
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(taskId);
    }
}
