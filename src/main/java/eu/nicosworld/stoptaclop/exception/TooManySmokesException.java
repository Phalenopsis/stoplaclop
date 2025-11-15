package eu.nicosworld.stoptaclop.exception;

public class TooManySmokesException extends RuntimeException {
  public TooManySmokesException() {
    super("Vous ne pouvez pas fumer plus d'une fois par minute");
  }
}
