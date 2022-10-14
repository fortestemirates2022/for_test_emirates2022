package com.emirates.flights.service;

import com.emirates.flights.controller.FlightsController;
import com.emirates.flights.db.FlightsRepository;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.time.LocalDate;

/**
 * The type Flights service.
 */
@Service
public class FlightsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FlightsService.class);
    private final FlightsRepository flightsRepository;
    LoadingCache<Tuple3<String, String, LocalDate>, String> cache
            = CacheBuilder.newBuilder().build(new CacheLoader<>() {
        @Override
        public String load(Tuple3<String, String, LocalDate> key) throws Exception {
            //emulating five slow downstream connections
            return Mono.when(connection1()
                    , connection2()
                    , connection3()
                    , connection4()
                    , connection5())
                    .then(Mono.just(flightsRepository.getFlightNumber(key.getT1()
                            , key.getT2()
                            , key.getT3()))).block();
        }

    });

    /**
     * Instantiates a new Flights service.
     *
     * @param flightsRepository the flights repository
     */
    public FlightsService(FlightsRepository flightsRepository) {
        this.flightsRepository = flightsRepository;
    }

    /**
     * Connection 1 mono.
     *
     * @return the mono
     */
    public Mono<String> connection1() {
        return Mono.just("1")
                .delayElement(Duration.ofMillis(getRandomNumber(500, 800)));
    }

    /**
     * Connection 2 mono.
     *
     * @return the mono
     */
    public Mono<String> connection2() {
        return Mono.just("2")
                .delayElement(Duration.ofMillis(getRandomNumber(500, 800)));
    }

    /**
     * Connection 3 mono.
     *
     * @return the mono
     */
    public Mono<String> connection3() {
        return Mono.just("3")
                .delayElement(Duration.ofMillis(getRandomNumber(500, 800)));
    }

    /**
     * Connection 4 mono.
     *
     * @return the mono
     */
    public Mono<String> connection4() {
        return Mono.just("4")
                .delayElement(Duration.ofMillis(getRandomNumber(500, 800)));
    }

    /**
     * Connection 5 mono.
     *
     * @return the mono
     */
    public Mono<String> connection5() {
        return Mono.just("5")
                .delayElement(Duration.ofMillis(getRandomNumber(500, 800)));
    }

    private int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    /**
     * Gets flight number.
     *
     * @param departure   the departure
     * @param destination the destination
     * @param date        the date
     * @return the flight number
     */
    public String getFlightNumber(String departure, String destination, LocalDate date) {
        String result = null;
        try {
            result = cache.getUnchecked(Tuples.of(departure, destination, date));
        }catch (Throwable throwable){
            LOGGER.warn("Flight from {} to {} on {} not found!", departure, destination, date);
            throw new FlightNotFoundException();
        }
        return result;
    }
}
