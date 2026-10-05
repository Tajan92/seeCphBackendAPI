package app.controller;

import app.dto.advert.AdvertDTORequest;
import app.dto.advert.AdvertDTOResponse;
import app.entities.Advert;
import app.service.AdvertService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;
@Slf4j
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
            log.error("401: unauthenticated");
            return;
        }
        ctx.status(HttpStatus.CREATED);
        log.info("201: Advert successfully created");
    }

    @Override
    public void getById(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).check(value -> value > 0, "Id must be positive").get();
        AdvertDTOResponse advertDTO = advertService.getById(id);
        if (advertDTO == null) {
            ctx.status(HttpStatus.NOT_FOUND);
            log.error("404: Advert not found");
            return;
        }
        ctx.status(HttpStatus.OK);
        ctx.json(advertDTO);
        log.info("200: Advert successfully retrieved");
    }

    @Override
    public void getAll(Context ctx) {
        List<AdvertDTOResponse> advertDTOs = advertService.getAll();
        if (advertDTOs.isEmpty()) {
            ctx.status(HttpStatus.NOT_FOUND);
            log.error("404: Adverts not found");
        }
        ctx.status(HttpStatus.OK);
        ctx.json(advertDTOs);
        log.info("200: Adverts successfully retrieved");
    }

    @Override
    public void updateById(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).check(value -> value > 0, "Id must be positive").get();
        AdvertDTORequest input = advertInputValidator(ctx);
        advertService.updateById(id, input);
        ctx.status(HttpStatus.OK);
        log.info("200: Advert successfully updated");
    }

    @Override
    public void deleteById(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).check(value -> value > 0, "Id must be positive").get();

        boolean deleted = advertService.deleteById(id);
        if (!deleted) {
            ctx.status(HttpStatus.NOT_FOUND);
            log.error("404: Advert not deleted");
        }
        ctx.status(HttpStatus.OK);
        log.info("200: Advert successfully deleted");
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
