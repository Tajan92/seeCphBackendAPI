package app.entities.users;

import app.entities.Advert;
import app.entities.Event;
import app.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SuperBuilder
public class Admin extends User {
    @OneToMany(mappedBy = "admin", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    //TODO: Decide fetchType and cascadeType
    private Set<Advert> adverts;

    public void addAdvert(Advert advert) {
        if (advert != null) {
            if (this.adverts == null) {
                this.adverts = new HashSet<>();
            }
            this.adverts.add(advert);
            advert.setAdmin(this);
        }
    }

    @OneToMany(mappedBy = "admin", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    //TODO: Decide fetchType and cascadeType
    private Set<Event> events;

    public void addEvent(Event event) {
        if (event != null) {
            if (this.events == null) {
                this.events = new HashSet<>();
            }
            this.events.add(event);
            event.setAdmin(this);
        }
    }
    @PrePersist
    public void prePersist() {
        this.addUserRole(UserRole.ADMIN);
    }
}
