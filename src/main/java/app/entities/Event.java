package app.entities;

import app.entities.users.Admin;
import app.entities.users.Attendee;
import app.entities.users.Organizer;
import app.enums.EventCategory;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "event")
@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Integer eventId;
    @Setter
    private String title;
    @Setter
    private String description;
    @Setter
    private Double price;
    private boolean free;
    private String location;
    @Setter
    private Double latitude;
    @Setter
    private Double longitude;
    @Setter
    @Column(name = "start_time")
    private LocalTime startTime;
    @Setter
    @Column(name = "end_time")
    private LocalTime endTime;

    @Setter
    @ElementCollection(fetch = FetchType.EAGER)//TODO: Decide fetchType
    @CollectionTable(name = "start_date", joinColumns = @JoinColumn(name = "start_date_id"))
    @Column(name = "start_date")
    private Set<LocalDate> startDates;

    @Setter
    @ElementCollection(fetch = FetchType.EAGER)//TODO: Decide fetchType
    @CollectionTable(name = "end_date", joinColumns = @JoinColumn(name = "end_date_id"))
    @Column(name = "end_date")
    private Set<LocalDate> endDates;

    @Setter
    @ElementCollection(targetClass = EventCategory.class, fetch = FetchType.EAGER)//TODO: Decide fetchType
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "event_categories", joinColumns = @JoinColumn(name = "event_categories_id"))
    @Column(name = "category")
    private Set<EventCategory> categories;

    @OneToMany(mappedBy = "event", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true) //TODO: Decide fetchType and cascadeType
    private Set<Advert> adverts;

    public void addAdvert(Advert advert) {
        if (advert != null) {
            if (this.adverts == null) {
                this.adverts = new HashSet<>();
            }
            this.adverts.add(advert);
            advert.setEvent(this);
        }
    }

    @ManyToOne
    @Setter
    private Organizer organizer;

    @ManyToOne
    @Setter
    private Admin admin;

    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer()
                .getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
                .getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass)
            return false;
        Event event = (Event) o;
        return getEventId() != null && Objects.equals(getEventId(), event.getEventId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
                .getPersistentClass()
                .hashCode() : getClass().hashCode();
    }
}
