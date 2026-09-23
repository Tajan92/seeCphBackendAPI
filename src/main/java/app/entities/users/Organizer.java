package app.entities.users;

import app.entities.Advert;
import app.entities.Event;
import app.enums.Status;
import app.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class Organizer extends User {
    @Column(name = "organizer_name")
    private String organizerName;

    @Enumerated(EnumType.STRING)
    private Status accountStatus;

    @OneToMany(mappedBy = "organizer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    //TODO: Decide fetchType and cascadeType
    private Set<Advert> adverts;

    public void addAdvert(Advert advert) {
        if (advert != null) {
            if (this.adverts == null) {
                this.adverts = new HashSet<>();
            }
            this.adverts.add(advert);
            advert.setOrganizer(this);
        }
    }


    @PrePersist
    public void prePersist() {
        this.addUserRole(UserRole.ORGANIZER);
    }

    @OneToMany(mappedBy = "organizer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    //TODO: Decide fetchType and cascadeType
    private Set<Event> events;

    public void addEvent(Event event) {
        if (event != null) {
            if (this.events == null) {
                this.events = new HashSet<>();
            }
            this.events.add(event);
            event.setOrganizer(this);
        }
    }
}
