package app.entities.events;

import app.entities.Advert;
import app.enums.EventCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Table(name = "event")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long eventId;

    private String title;
    private String description;
    private Double price;
    private String location;
    private Double latitude;
    private Double longitude;
    @Column(name = "start_time")
    private LocalTime startTime;
    @Column(name = "end_time")
    private LocalTime endTime;

    @ElementCollection(fetch = FetchType.EAGER)//TODO: Decide fetchType
    @CollectionTable(name = "start_date", joinColumns = @JoinColumn(name = "start_date_id"))
    @Column(name = "start_date")
    Set<LocalDate> startDates;

    @ElementCollection(fetch = FetchType.EAGER)//TODO: Decide fetchType
    @CollectionTable(name = "end_date", joinColumns = @JoinColumn(name = "end_date_id"))
    @Column(name = "end_date")
    Set<LocalDate> endDates;

    @ElementCollection(targetClass = EventCategory.class)//TODO: Decide fetchType
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "event_categories", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "category")
    private Set<EventCategory> eventCategory;

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY, cascade = CascadeType.ALL) //TODO: Decide fetchType and cascadeType
    private Set<Advert> advert;

    private void addAdvert(Advert advert) {
        this.advert.add(advert);
        if (advert != null) {
            advert.setEvent(this);
        }
    }
}
