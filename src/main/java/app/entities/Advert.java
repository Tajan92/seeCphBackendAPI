package app.entities;

import app.entities.events.Event;
import app.entities.users.Admin;
import app.entities.users.Organizer;
import app.entities.users.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

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
