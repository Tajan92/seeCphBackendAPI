package app.controller;

import app.dto.advert.AdvertDTORequest;
import app.dto.advert.AdvertDTOResponse;
import app.entities.Advert;
import app.service.AdvertService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;

public class AdvertHandler implements IHandler {
    AdvertService advertService;

    public AdvertHandler(AdvertService advertService) {
        this.advertService = advertService;
    }

    @Override
    public void create(Context ctx) {
        AdvertDTORequest input = advertInputValidator(ctx);
        AdvertDTOResponse advertDTOResponse = advertService.create(input);
        if (advertDTOResponse == null) {
            ctx.status(HttpStatus.UNAUTHORIZED);
            return;
        }
        ctx.status(HttpStatus.CREATED);
    }

    @Override
    public void getById(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).check(value -> value > 0, "Id must be positive").get();
        AdvertDTOResponse advertDTO = advertService.getById(id);
        if (advertDTO == null) {
            ctx.status(HttpStatus.NOT_FOUND);
            return;
        }
        ctx.status(HttpStatus.OK);
        ctx.json(advertDTO);
    }

    @Override
    public void getAll(Context ctx) {
        List<AdvertDTOResponse> advertDTOs = advertService.getAll();
        if (advertDTOs.isEmpty()) {
            ctx.status(HttpStatus.NOT_FOUND);
        }
        ctx.status(HttpStatus.OK);
        ctx.json(advertDTOs);
    }

    @Override
    public void updateById(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).check(value -> value > 0, "Id must be positive").get();
        AdvertDTORequest input = advertInputValidator(ctx);
        advertService.updateById(id, input);
        ctx.status(HttpStatus.OK);
    }

    @Override
    public void deleteById(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).check(value -> value > 0, "Id must be positive").get();

        boolean deleted = advertService.deleteById(id);
        if (!deleted) {
            ctx.status(HttpStatus.NOT_FOUND);
        }
        ctx.status(HttpStatus.OK);
    }

    private AdvertDTORequest advertInputValidator(Context ctx) {
        return ctx.bodyValidator(AdvertDTORequest.class)
                .check(advert -> advert.addPlacement() != null && !advert.addPlacement().toString().isEmpty(), "Ad placement must be defined")
                .check(advert -> advert.startDate() != null, "Please add start date")
                .check(advert -> advert.startDate().isAfter(LocalDate.now()) || advert.startDate().isEqual(LocalDate.now()), "Start date must be today or in the future")
                .check(advert -> advert.endDate() != null, "Please add end date")
                .check(advert -> advert.endDate().isAfter(advert.startDate()) || advert.endDate().isEqual(advert.startDate()), "End date can't be before start date").get();
    }
}
