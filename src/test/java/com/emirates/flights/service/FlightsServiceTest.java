package com.emirates.flights.service;

import com.emirates.flights.db.FlightsRepository;
import com.google.common.base.Stopwatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FlightsServiceTest {

    @Mock
    private FlightsRepository mockFlightsRepository;

    private FlightsService flightsServiceUnderTest;

    @BeforeEach
    void setUp() {
        flightsServiceUnderTest = new FlightsService(mockFlightsRepository);
    }

    @Test
    void testConnection1() {
        // Setup
        // Run the test
        Stopwatch stopwatch = Stopwatch.createStarted();
        final String result = flightsServiceUnderTest.connection1().block();
        stopwatch.stop();
        assertEquals("1", result);
        assertTrue(stopwatch.elapsed(TimeUnit.MILLISECONDS)>=500&&stopwatch.elapsed(TimeUnit.MILLISECONDS)<=800);
    }

    @Test
    void testConnection2() {
        // Setup
        // Run the test
        Stopwatch stopwatch = Stopwatch.createStarted();
        final String result = flightsServiceUnderTest.connection2().block();
        stopwatch.stop();
        assertEquals("2", result);
        assertTrue(stopwatch.elapsed(TimeUnit.MILLISECONDS)>=500&&stopwatch.elapsed(TimeUnit.MILLISECONDS)<=800);

    }

    @Test
    void testConnection3() {
        // Setup
        // Run the test
        Stopwatch stopwatch = Stopwatch.createStarted();
        final String result = flightsServiceUnderTest.connection3().block();
        stopwatch.stop();
        assertEquals("3", result);
        assertTrue(stopwatch.elapsed(TimeUnit.MILLISECONDS)>=500&&stopwatch.elapsed(TimeUnit.MILLISECONDS)<=800);

    }

    @Test
    void testConnection4() {
        // Setup
        // Run the test
        Stopwatch stopwatch = Stopwatch.createStarted();
        final String result = flightsServiceUnderTest.connection4().block();
        stopwatch.stop();
        assertEquals("4", result);
        assertTrue(stopwatch.elapsed(TimeUnit.MILLISECONDS)>=500&&stopwatch.elapsed(TimeUnit.MILLISECONDS)<=800);

    }

    @Test
    void testConnection5() {
        // Setup
        // Run the test
        Stopwatch stopwatch = Stopwatch.createStarted();
        final String result = flightsServiceUnderTest.connection5().block();
        stopwatch.stop();
        assertEquals("5", result);
        assertTrue(stopwatch.elapsed(TimeUnit.MILLISECONDS)>=500&&stopwatch.elapsed(TimeUnit.MILLISECONDS)<=800);

    }

    @Test
    void testGetFlightNumberSuccess() {
        // Setup

        when(mockFlightsRepository.getFlightNumber("LED", "DXB"
                , LocalDate.of(2022, 1, 1))).thenReturn("LED_DXB_2022-01-01");

        // Run the test
        Stopwatch stopwatch = Stopwatch.createStarted();
        final String result = flightsServiceUnderTest.getFlightNumber("LED", "DXB",
                LocalDate.of(2022, 1, 1));
        stopwatch.stop();
        assertTrue(stopwatch.elapsed(TimeUnit.MILLISECONDS)>=500&&stopwatch.elapsed(TimeUnit.MILLISECONDS)<=850);
        // Verify the results
        assertThat(result).isEqualTo("LED_DXB_2022-01-01");
    }

    @Test
    void testGetFlightNumberException() {
        // Setup
        when(mockFlightsRepository.getFlightNumber("LED", "DXB"
                , LocalDate.of(2022, 1, 1))).thenReturn("LED_DXB_2022-01-01");
        // Verify the results
        FlightNotFoundException thrown = assertThrows(
                FlightNotFoundException.class,
                () -> flightsServiceUnderTest.getFlightNumber("DXB", "DXB",
                        LocalDate.of(2022, 1, 1)),
                "Expected getFlightNumber() to throw an exception, but it didn't"
        );

        assertTrue(thrown.getMessage()==null);
    }
}
