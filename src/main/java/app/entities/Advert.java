package app.entities;

import app.entities.users.Admin;
import app.entities.users.Organizer;
import app.enums.AddPlacement;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "advert")
public class Advert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "advert_id")
    private Integer advertId;
    private AddPlacement addPlacement;
    private Double price;
    @Column(name = "start_date")
    private LocalDate startDate;
    @Column(name = "end_date")
    private LocalDate endDate;
    private boolean status;

    @ManyToOne(fetch = FetchType.LAZY) //TODO: Decide fetchType and cascadeType
    @Setter
    private Organizer organizer;

    @ManyToOne(fetch = FetchType.LAZY) //TODO: Decide fetchType and cascadeType
    @Setter
    private Admin admin;

    @ManyToOne(fetch = FetchType.LAZY) //TODO: Decide fetchType and cascadeType
    @Setter
    private Event event;

    @PrePersist
    private void prePersist() {
        this.status = !LocalDate.now().isBefore(startDate) && !LocalDate.now().isAfter(endDate);
    }

    @PreUpdate
    private void preUpdate() {
        this.status = LocalDate.now().isBefore(startDate) && !LocalDate.now().isAfter(endDate);
    }
}
