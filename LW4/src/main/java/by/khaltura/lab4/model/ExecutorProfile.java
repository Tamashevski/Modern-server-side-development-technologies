package by.khaltura.lab4.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

// Профиль исполнителя. Связан с пользователем один к одному,
// первичный ключ профиля совпадает с user_id (аннотация @MapsId)
@Entity
@Table(name = "executor_profile")
public class ExecutorProfile {

    @Id
    @Column(name = "user_id")
    private Integer userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private AppUser user;

    @Column(name = "specialization", nullable = false, length = 100)
    private String specialization;

    @Column(name = "skills", columnDefinition = "TEXT")
    private String skills;

    @Column(name = "location")
    private String location;

    @Column(name = "hourly_rate", precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    public ExecutorProfile() {
    }

    public ExecutorProfile(AppUser user, String specialization, String skills, String location, BigDecimal hourlyRate) {
        this.user = user;
        this.specialization = specialization;
        this.skills = skills;
        this.location = location;
        this.hourlyRate = hourlyRate;
    }

    public Integer getUserId() { return userId; }
    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public BigDecimal getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; }

    @Override
    public String toString() {
        return String.format("Профиль [%s; навыки: %s; город: %s; ставка: %s BYN/ч]",
                specialization, skills == null ? "–" : skills, location == null ? "–" : location,
                hourlyRate == null ? "–" : hourlyRate);
    }
}
