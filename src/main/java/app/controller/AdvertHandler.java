package app.controller;

import app.dto.advert.AdvertDTORequest;
import app.dto.advert.AdvertDTOResponse;
import app.dto.user.UserDTOResponse;
import app.entities.Advert;
import app.enums.AddPlacement;
import app.service.AdvertService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;
@Slf4j
public class AdvertHandler implements IHandler {
    private final AdvertService advertService;

    public AdvertHandler(AdvertService advertService) {
        this.advertService = advertService;
    }

    @Override
    public void create(Context ctx) {
        UserDTOResponse currentUser = ctx.attribute("user");
        AdvertDTORequest input = advertInputValidator(ctx);
        ctx.status(HttpStatus.CREATED).json(advertService.create(input, currentUser.id()));
    }

    @Override
    public void getById(Context ctx) {
        ctx.status(HttpStatus.OK).json(advertService.getById(getId(ctx)));
    }

    @Override
    public void getAll(Context ctx) {
        ctx.status(HttpStatus.OK).json(advertService.getAll());
    }

    /** Adverts owned by the logged-in organizer/admin. */
    public void getMyAdverts(Context ctx) {
        UserDTOResponse currentUser = ctx.attribute("user");
        ctx.status(HttpStatus.OK).json(advertService.getByUserId(currentUser.id()));
    }

    /** Public: adverts running today, optionally for one placement (?placement=FRONTPAGE_HEADER). */
    public void getActiveAdverts(Context ctx) {
        AddPlacement placement = ctx.queryParamAsClass("placement", AddPlacement.class).allowNullable().get();
        ctx.status(HttpStatus.OK).json(advertService.getActive(placement));
    }

    @Override
    public void updateById(Context ctx) {
        int id = getId(ctx);
        AdvertDTORequest input = advertInputValidator(ctx);
        ctx.status(HttpStatus.OK).json(advertService.updateById(id, input));
    }

    @Override
    public void deleteById(Context ctx) {
        advertService.deleteById(getId(ctx));
        ctx.status(HttpStatus.NO_CONTENT);
    }

    private static int getId(Context ctx) {
        return ctx.pathParamAsClass("id", Integer.class)
                .check(value -> value > 0, "Id must be positive")
                .get();
    }

    private AdvertDTORequest advertInputValidator(Context ctx) {
        return ctx.bodyValidator(AdvertDTORequest.class)
                .check(a -> a.eventId() > 0, "Event id must be positive")
                .check(a -> a.addPlacement() != null, "Ad placement must be defined")
                .check(a -> a.startDate() != null && !a.startDate().isBefore(LocalDate.now()), "Start date must be today or in the future")
                .check(a -> a.endDate() != null && a.startDate() != null && !a.endDate().isBefore(a.startDate()), "End date must be on or after the start date")
                .get();
    }
}
