package BankingApplication.Banking.exceptions;

public class InvalidAddressException extends Exception {
    public InvalidAddressException(String message) {
        super(message);
    }
}