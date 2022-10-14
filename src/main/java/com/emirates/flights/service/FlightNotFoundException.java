package com.emirates.flights.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "flight not found")
public class FlightNotFoundException extends RuntimeException {
}