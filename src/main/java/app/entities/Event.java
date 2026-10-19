package app.entities;

import app.entities.users.Admin;
import app.entities.users.Attendee;
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
public class Event implements IGetId {
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
    @Setter
    private boolean free;
    @Setter
    @ManyToOne(cascade = {CascadeType.MERGE})
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
    private String apiEventId;
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
    @Builder.Default
    private Set<Advert> adverts = new HashSet<>();

    @ManyToOne
    @Setter
    private Organizer organizer;

    @ManyToOne
    @Setter
    private Admin admin;

    @Builder.Default
    @ManyToMany(mappedBy = "favoriteEvents")
    private Set<Attendee> attendeeFavorites = new HashSet<>();

    @Builder.Default
    @ManyToMany(mappedBy = "likedEvents")
    private Set<Attendee> attendeeLikes = new HashSet<>();

    public void addAdvert(Advert advert) {
        if (advert != null) {
            if (this.adverts == null) {
                this.adverts = new HashSet<>();
            }
            this.adverts.add(advert);
            advert.setEvent(this);
        }
    }

    public void removeAdvert(Advert advert) {
        if (advert != null && this.adverts != null) {
            this.adverts.remove(advert);
            advert.setEvent(null);
        }
    }

    @Override
    public Integer getId() {
        return eventId;
    }

    @PreRemove
    void removeFromAttendees(){
        for (Attendee attendee : attendeeFavorites) {
            attendee.removeFavoriteEvent(this);
        }
        for (Attendee attendee : attendeeLikes) {
            attendee.removeLikedEvent(this);
        }
        if (organizer != null && organizer.getEvents() != null) {
            organizer.getEvents().remove(this);
        }
        if (admin != null && admin.getEvents() != null) {
            admin.getEvents().remove(this);
        }
    }

    @PrePersist
    public void prePersist() {
        lastSyncedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        lastSyncedAt = LocalDateTime.now();
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
