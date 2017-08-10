package demo.journals.service;

class ServiceException extends RuntimeException {

    ServiceException(String message) {
        super(message);
    }

    ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
