package com.emirates.flights.service;

import com.emirates.flights.db.FlightsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.util.function.Tuple4;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * The type Price service.
 */
@Service
public class PriceService {
    private final FlightsRepository flightsRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(PriceService.class);
    @Value("${flights.app.kmPrice:0.05}")
    private double kmPrice;
    @Value("${flights.app.lastmomentMultiplier:1.5}")
    private double lastmomentMultiplier;
    @Value("${flights.app.minPrice:300}")
    private double minPrice;
    @Value("${flights.app.weekendsMultiplier:1.3}")
    private double weekendsMultiplier;

    /**
     * Instantiates a new Price service.
     *
     * @param flightsRepository the flights repository
     */
    @Autowired
    public PriceService(FlightsRepository flightsRepository,
                        @Value("${flights.app.kmPrice:0.05}") double kmPrice,
                        @Value("${flights.app.lastmomentMultiplier:1.5}") double lastmomentMultiplier,
                        @Value("${flights.app.minPrice:300}") double minPrice,
                        @Value("${flights.app.weekendsMultiplier:1.3}") double weekendsMultiplier) {
        this.flightsRepository = flightsRepository;
        this.weekendsMultiplier = weekendsMultiplier;
        this.lastmomentMultiplier = lastmomentMultiplier;
        this.minPrice = minPrice;
        this.kmPrice = kmPrice;
    }

    /**
     * Calculate price big decimal.
     *
     * @param flightNumber the flight number
     * @param date         the date
     * @return the big decimal
     */
    public BigDecimal calculatePrice(String flightNumber, LocalDate date) {
        double price;
        try {
            price = calculate(flightNumber, date);
        } catch (Throwable throwable) {
            LOGGER.warn("Flight {} on {} not found!", flightNumber, date);
            throw new FlightNotFoundException();
        }
        return new BigDecimal(price);
    }

    /**
     * Calculate double.
     *
     * @param flightNumber the flight number
     * @param date         the date
     * @return the double
     */
    public double calculate(String flightNumber, LocalDate date) {
        Tuple4<BigDecimal, BigDecimal, BigDecimal, BigDecimal> coordinates = flightsRepository.getCoordinates(flightNumber, date);
        double distance = distance(coordinates.getT1().doubleValue(), coordinates.getT2().doubleValue(), coordinates.getT3().doubleValue(), coordinates.getT4().doubleValue());
        double price = minPrice + distance * kmPrice;
        if (isLastMoment(date)) {
            price = price * lastmomentMultiplier;
        }
        if (isWeekend(date)) {
            price = price * weekendsMultiplier;
        }
        return price;
    }

    /**
     * Is weekend boolean.
     *
     * @param date the date
     * @return the boolean
     */
    public boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
    }

    /**
     * Is last moment boolean.
     *
     * @param date the date
     * @return the boolean
     */
    public boolean isLastMoment(LocalDate date) {
        return date.isBefore(LocalDate.now().plusDays(3));
    }

    /**
     * Distance double.
     *
     * @param lon1 the lon 1
     * @param lat1 the lat 1
     * @param lon2 the lon 2
     * @param lat2 the lat 2
     * @return the double
     */
    public double distance(double lon1, double lat1, double lon2, double lat2) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        } else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515 * 1.609344;
            return dist;
        }
    }
}
