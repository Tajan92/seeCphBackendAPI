package app.entities;

import app.entities.users.Admin;
import app.entities.users.Organizer;
import app.enums.EventCategory;
import app.enums.SourceProvider;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @Column(columnDefinition = "TEXT")
    private String description;
    @Setter
    private Double price;
    private boolean free;
    @Setter
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "location_id")
    private Address location;
    @Setter
    private String latitude;
    @Setter
    private String longitude;
    @Setter
    @Column(name = "start_time")
    private LocalTime startTime;
    @Setter
    @Column(name = "start_date")
    private LocalDate startDate;
    @Setter
    private String url;
    @Setter
    @Column(name = "source_provider")
    private SourceProvider sourceProvider;
    @Setter
    @Column(name = "source_event_id")
    private String sourceEventId;
    @Setter
    @Column(name = "last_synced_at")
    private LocalDateTime lastSyncedAt;
    @Setter
    @Column(name = "image_url")
    private String imageUrl;
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "event_category")
    private EventCategory category;

    @OneToMany(mappedBy = "event", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
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

    @PrePersist
    public void prePersist() {
        lastSyncedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        lastSyncedAt = LocalDateTime.now();
    }

    public void addDescription(String description) {
        this.description = this.description + description;
    }

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
