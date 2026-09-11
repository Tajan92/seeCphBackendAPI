package app.entities;

import app.entities.users.Admin;
import app.entities.users.Organizer;
import app.enums.AddPlacement;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.util.Objects;

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
    @Setter
    private AddPlacement addPlacement;
    @Setter
    private Double price;
    @Setter
    @Column(name = "start_date")
    private LocalDate startDate;
    @Setter
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
        Advert advert = (Advert) o;
        return getAdvertId() != null && Objects.equals(getAdvertId(), advert.getAdvertId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
                .getPersistentClass()
                .hashCode() : getClass().hashCode();
    }
}
