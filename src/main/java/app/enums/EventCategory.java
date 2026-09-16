package app.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;


public enum EventCategory {

    // --- Miscellaneous ---
    CASINO_GAMING("Casino/Gaming"),
    COMMUNITY_CIVIC("Community/Civic"),
    COMMUNITY_CULTURAL("Community/Cultural"),
    FAIRS_FESTIVALS("Fairs & Festivals"),
    FOOD_DRINK("Food & Drink"),
    HEALTH_WELLNESS("Health/Wellness"),
    HOBBY_SPECIAL_INTEREST_EXPOS("Hobby/Special Interest Expos"),
    ICE_SHOWS("Ice Shows"),
    LECTURE_SEMINAR("Lecture/Seminar"),
    PSYCHICS_MEDIUMS_HYPNOTISTS("Psychics/Mediums/Hypnotists"),
    SPECIAL_INTEREST_HOBBY("Special Interest/Hobby"),

    // --- Sports ---
    AQUATICS("Aquatics"),
    ATHLETIC_RACES("Athletic Races"),
    BADMINTON("Badminton"),
    BANDY("Bandy"),
    BASEBALL("Baseball"),
    BASKETBALL("Basketball"),
    BIATHLON("Biathlon"),
    BODY_BUILDING("Body Building"),
    BOXING("Boxing"),
    CRICKET("Cricket"),
    CURLING("Curling"),
    CYCLING("Cycling"),
    EQUESTRIAN("Equestrian"),
    ESPORTS("eSports"),
    EXTREME("Extreme"),
    FIELD_HOCKEY("Field Hockey"),
    FITNESS("Fitness"),
    FLOORBALL("Floorball"),
    FOOTBALL("Football"),
    GOLF("Golf"),
    GYMNASTICS("Gymnastics"),
    HANDBALL("Handball"),
    HOCKEY("Hockey"),
    ICE_SKATING("Ice Skating"),
    INDOOR_SOCCER("Indoor Soccer"),
    LACROSSE("Lacrosse"),
    MARTIAL_ARTS("Martial Arts"),
    MOTORSPORTS_RACING("Motorsports/Racing"),
    NETBALL("Netball"),
    RODEO("Rodeo"),
    ROLLER_DERBY("Roller Derby"),
    ROLLER_HOCKEY("Roller Hockey"),
    RUGBY("Rugby"),
    SKI_JUMPING("Ski Jumping"),
    SKIING("Skiing"),
    SOCCER("Soccer"),
    SOFTBALL("Softball"),
    SQUASH("Squash"),
    SURFING("Surfing"),
    SWIMMING("Swimming"),
    TABLE_TENNIS("Table Tennis"),
    TENNIS("Tennis"),
    TOROS("Toros"),
    TRACK_FIELD("Track & Field"),
    VOLLEYBALL("Volleyball"),
    WATERPOLO("Waterpolo"),
    WRESTLING("Wrestling"),
    RINGUETTE("Ringuette"),
    PADEL("Padel"),
    CHEERLEADING("Cheerleading"),
    DIVING("Diving"),
    PICKLEBALL("Pickleball"),

    // --- Music ---
    ALTERNATIVE("Alternative"),
    BALLADS_ROMANTIC("Ballads/Romantic"),
    BLUES("Blues"),
    CHANSON_FRANCAISE("Chanson Francaise"),
    CHILDRENS_MUSIC("Children's Music"),
    CLASSICAL("Classical"),
    COUNTRY("Country"),
    DANCE_ELECTRONIC("Dance/Electronic"),
    FOLK("Folk"),
    HIP_HOP_RAP("Hip-Hop/Rap"),
    JAZZ("Jazz"),
    LATIN("Latin"),
    MEDIEVAL_RENAISSANCE("Medieval/Renaissance"),
    METAL("Metal"),
    NEW_AGE("New Age"),
    OTHER("Other"),
    POP("Pop"),
    R_AND_B("R&B"),
    REGGAE("Reggae"),
    RELIGIOUS("Religious"),
    ROCK("Rock"),
    WORLD("World"),

    // --- Arts & Theatre ---
    CHILDRENS_THEATRE("Children's Theatre"),
    CIRCUS_SPECIALTY_ACTS("Circus & Specialty Acts"),
    CULTURAL("Cultural"),
    DANCE("Dance"),
    ESPECTACULO("Espectaculo"),
    FASHION("Fashion"),
    FINE_ART("Fine Art"),
    MAGIC_ILLUSION("Magic & Illusion"),
    MISCELLANEOUS_THEATRE("Miscellaneous Theatre"),
    OPERA("Opera"),
    PERFORMANCE_ART("Performance Art"),
    PUPPETRY("Puppetry"),
    SPECTACULAR("Spectacular"),
    THEATRE("Theatre"),
    VARIETY("Variety"),

    // --- Film ---
    ACTION_ADVENTURE("Action/Adventure"),
    ANIMATION("Animation"),
    ARTHOUSE("Arthouse"),
    DOCUMENTARY("Documentary"),
    DRAMA("Drama"),
    FOREIGN("Foreign"),
    HORROR("Horror"),
    SCIENCE_FICTION("Science Fiction"),
    URBAN("Urban"),

    // --- Other categories ---
    COMEDY("Comedy"),
    FAMILY("Family"),
    HOLIDAY("Holiday"),
    MISCELLANEOUS("Miscellaneous"),
    MULTIMEDIA("Multimedia"),
    MUSIC("Music"),

    // --- Default---
    OTHERS("Other");

    private final String label;

    EventCategory(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    private static final Map<String, EventCategory> events = new HashMap<>();
    static {
        for (EventCategory category : values()) {
            events.put(category.label.toLowerCase(), category);
        }
    }

    @JsonCreator
    public static EventCategory fromLabel(String label) {
        if (label == null) return OTHERS;
        return events.getOrDefault(label.toLowerCase(), OTHERS);
    }
}