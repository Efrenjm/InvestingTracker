package org.efrenjm.investingtracker.interfaces.rest.exception.base;

public abstract class ServerErrorException extends HttpException {
    protected ServerErrorException(
            int statusCode, String errorCode, String message, Throwable cause) {
        super(statusCode, errorCode, message, cause);
    }

    protected ServerErrorException(int statusCode, String errorCode, String message) {
        super(statusCode, errorCode, message, null);
    }
}
