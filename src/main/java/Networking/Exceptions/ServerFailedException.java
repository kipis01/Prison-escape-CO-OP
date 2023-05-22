package Networking.Exceptions;

public class ServerFailedException extends Exception {
    public ServerFailedException(String errorMessage){
        super(errorMessage);
    }
}
