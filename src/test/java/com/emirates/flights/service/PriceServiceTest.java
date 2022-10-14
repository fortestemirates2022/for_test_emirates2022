package com.emirates.flights.service;

import com.emirates.flights.db.FlightsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.util.function.Tuples;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @Mock
    private FlightsRepository mockFlightsRepository;

    private PriceService priceServiceUnderTest;

    @BeforeEach
    void setUp() {
        priceServiceUnderTest = new PriceService(mockFlightsRepository, 0.005, 300, 1.5, 1.4);
    }

    @Test
    void testCalculatePrice() {
        // Setup
        when(mockFlightsRepository.getCoordinates("flightNumber", LocalDate.of(2020, 1, 1)))
                .thenReturn(Tuples.of(
                        new BigDecimal("61.556400299072266"),
                        new BigDecimal("69.76329803466797"),
                        new BigDecimal("46.6974983215332"),
                        new BigDecimal("61.235801696777344")
                ));

        // Run the test
        final BigDecimal result = priceServiceUnderTest.calculatePrice("flightNumber", LocalDate.of(2020, 1, 1));

        // Verify the results
        assertThat(result.equals(new BigDecimal("2194.7265933368835")));
    }

    @Test
    void testCalculatePriceException() {
        // Setup
        when(mockFlightsRepository.getCoordinates("flightNumber", LocalDate.of(2020, 1, 1)))
                .thenReturn(Tuples.of(
                        new BigDecimal("61.556400299072266"),
                        new BigDecimal("69.76329803466797"),
                        new BigDecimal("46.6974983215332"),
                        new BigDecimal("61.235801696777344")
                ));

        // Run the test
        FlightNotFoundException thrown = assertThrows(
                FlightNotFoundException.class,
                () -> priceServiceUnderTest
                        .calculatePrice("fail", LocalDate.of(2020, 1, 1))
                ,
                "Expected getFlightNumber() to throw an exception, but it didn't"
        );

        assertTrue(thrown.getMessage() == null);
    }

    @Test
    void testCalculate() {
        // Setup
        when(mockFlightsRepository.getCoordinates("flightNumber", LocalDate.of(2020, 1, 1)))
                .thenReturn(Tuples.of(
                        new BigDecimal("61.556400299072266"),
                        new BigDecimal("69.76329803466797"),
                        new BigDecimal("46.6974983215332"),
                        new BigDecimal("61.235801696777344")
                ));

        // Run the test
        final double result = priceServiceUnderTest.calculate("flightNumber", LocalDate.of(2020, 1, 1));

        // Verify the results
        assertThat(result).isEqualTo(2194.7265933368835, within(0.0001));
    }

    @Test
    void testIsWeekend() {
        assertThat(priceServiceUnderTest.isWeekend(LocalDate.of(2020, 1, 1))).isFalse();
    }

    @Test
    void testIsLastMoment() {
        assertThat(priceServiceUnderTest.isLastMoment(LocalDate.of(2023, 1, 1))).isFalse();
    }

    @Test
    void testDistance() {
        assertThat(priceServiceUnderTest.distance(0.0, 0.0, 0.0, 0.0)).isEqualTo(0.0, within(0.0001));
    }
}
