package com.emirates.flights.controller;

import com.emirates.flights.controller.ro.FlightResponseRO;
import com.emirates.flights.controller.ro.PriceResponseRO;
import com.emirates.flights.service.FlightsService;
import com.emirates.flights.service.PriceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Flights controller.
 */
@RestController
@RequestMapping("api/v1")
@Api
public class FlightsController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FlightsController.class);
    private final FlightsService flightsService;
    private final PriceService priceService;
    @Autowired
    public FlightsController(FlightsService flightsService, PriceService priceService) {
        this.flightsService = flightsService;
        this.priceService = priceService;
    }

    /**
     * Gets the flight number.
     *
     * @param departureAirport   the departure airport
     * @param destinationAirport the destination airport
     * @param departureDate      the departure date
     * @return flight number.
     */
    @ApiOperation(value = "Flight number.", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "500", description = "Error occurred"),
            @ApiResponse(responseCode = "404", description = "Flight not found")
    })
    @GetMapping("/flights")
    public FlightResponseRO getFlight(@RequestParam String departureAirport, @RequestParam String destinationAirport
            , @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Validated LocalDate departureDate) {
        String result = flightsService.getFlightNumber(departureAirport, destinationAirport, departureDate);
        LOGGER.trace("Flight from {} to {} on {} is {}",departureAirport, destinationAirport, departureDate, result);
        return new FlightResponseRO(result);
    }

    /**
     * Gets the flight price.
     *
     * @param flightNumber  the flight number
     * @param departureDate the departure date
     * @return flight price.
     */
    @ApiOperation(value = "Flight price.", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "500", description = "Error occurred"),
            @ApiResponse(responseCode = "404", description = "Flight not found")
    })
    @GetMapping("/price")
    public PriceResponseRO getPrice(@RequestParam String flightNumber
            , @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Validated LocalDate departureDate) {
        BigDecimal result =priceService.calculatePrice(flightNumber, departureDate);
        LOGGER.trace("Price for {} on {}  is {}",flightNumber, departureDate, result);
        return new PriceResponseRO(result);
    }

    /**
     * Handle exceptions.
     *
     * @param ex the ex
     * @return the map
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return errors;
    }
}
