package com.emirates.flights.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple4;
import reactor.util.function.Tuples;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * The type Flights repository.
 */
@Repository
public class FlightsRepository {
    private final JdbcTemplate jdbcTemplate;

    /**
     * Instantiates a new Flights repository.
     *
     * @param jdbcTemplate the jdbc template
     */
    @Autowired
    public FlightsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Gets airport coordinates.
     *
     * @param airportCode the airport code
     * @return the airport coordinates
     */
    public Tuple2<BigDecimal, BigDecimal> getAirportCoordinates(String airportCode) {
        return jdbcTemplate.queryForObject("select longtitude, latitude from airports where airport_code = ?",
                new Object[]{airportCode},
                (rs, rowNum) -> Tuples.of(rs.getBigDecimal(1), rs.getBigDecimal(2)));
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
        return jdbcTemplate.queryForObject("SELECT f.flight_number " +
                        "FROM flights AS f " +
                        "JOIN airports AS dest ON f.destination_id = dest.id " +
                        "JOIN airports AS dep ON f.departure_id = dep.id " +
                        "WHERE dep.airport_code=? AND dest.airport_code=? AND f.flight_date=?",
                new Object[]{departure, destination, date},
                String.class);
    }

    /**
     * Gets coordinates.
     *
     * @param flightNumber the flight number
     * @param date         the date
     * @return the coordinates
     */
    public Tuple4<BigDecimal, BigDecimal, BigDecimal, BigDecimal> getCoordinates(String flightNumber, LocalDate date) {
        return jdbcTemplate.queryForObject("SELECT dep.longtitude, dep.latitude, dest.longtitude, dest.latitude " +
                        "FROM flights AS f " +
                        "JOIN airports AS dest ON f.destination_id = dest.id " +
                        "JOIN airports AS dep ON f.departure_id = dep.id " +
                        "WHERE f.flight_number=? AND f.flight_date=?",
                new Object[]{flightNumber, date},
                (rs, rowNum) -> Tuples.of(rs.getBigDecimal(1)
                        , rs.getBigDecimal(2)
                        , rs.getBigDecimal(3)
                        , rs.getBigDecimal(4)));
    }
}
