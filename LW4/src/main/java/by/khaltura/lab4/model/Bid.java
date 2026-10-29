package by.khaltura.lab4.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

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
import jakarta.persistence.Table;

@Entity
@Table(name = "bid")
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bid_id")
    private Integer bidId;

    // Вторая сторона двусторонней связи Task ↔ Bid. Внешний ключ task_id хранится здесь
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "executor_id", nullable = false)
    private AppUser executor;

    @Column(name = "proposed_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal proposedPrice;

    @Column(name = "deadline", nullable = false)
    private LocalDate deadline;

    @Column(name = "proposal_text", nullable = false, columnDefinition = "TEXT")
    private String proposalText;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private BidStatus status = BidStatus.PENDING;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public Bid() {
    }

    public Bid(Task task, AppUser executor, BigDecimal proposedPrice, LocalDate deadline, String proposalText) {
        this.task = task;
        this.executor = executor;
        this.proposedPrice = proposedPrice;
        this.deadline = deadline;
        this.proposalText = proposalText;
    }

    public Integer getBidId() { return bidId; }
    public Task getTask() { return task; }
    public void setTask(Task task) { this.task = task; }
    public AppUser getExecutor() { return executor; }
    public BigDecimal getProposedPrice() { return proposedPrice; }
    public void setProposedPrice(BigDecimal proposedPrice) { this.proposedPrice = proposedPrice; }
    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    public String getProposalText() { return proposalText; }
    public void setProposalText(String proposalText) { this.proposalText = proposalText; }
    public BidStatus getStatus() { return status; }
    public void setStatus(BidStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return String.format("Отклик [id=%d, %s, %s BYN, до %s, %s] «%s»",
                bidId, executor.getFullName(), proposedPrice, deadline, status.getTitle(), proposalText);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bid other)) return false;
        return bidId != null && bidId.equals(other.bidId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(bidId);
    }
}
