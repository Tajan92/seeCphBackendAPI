package app.entities.users;

import app.entities.Advert;
import app.entities.Event;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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

    private void addAdvert(Advert advert) {
        this.adverts.add(advert);
        if (advert != null) {
            advert.setAdmin(this);
        }
    }

    @OneToMany(mappedBy = "admin", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    //TODO: Decide fetchType and cascadeType
    private Set<Event> events;

    private void addEvent(Event event) {
        this.events.add(event);
        if (event != null) {
            event.setAdmin(this);
        }
    }
}
