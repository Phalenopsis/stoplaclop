package eu.nicosworld.stoptaclop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class TooManySmokesException extends RuntimeException {
    public TooManySmokesException() {
        super("Vous ne pouvez pas fumer plus d'une fois par minute");
    }
}
