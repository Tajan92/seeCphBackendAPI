package app.entities;

import app.entities.users.Admin;
import app.entities.users.Organizer;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "advert")
public class Advert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "advert_id")
    private Integer advertId;

    private String placement;
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
}
