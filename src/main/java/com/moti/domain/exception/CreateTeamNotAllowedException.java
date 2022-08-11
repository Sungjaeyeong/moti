package com.moti.domain.exception;

public class CreateTeamNotAllowedException extends RuntimeException{

    public CreateTeamNotAllowedException() {
        super();
    }

    public CreateTeamNotAllowedException(String message) {
        super(message);
    }
}
