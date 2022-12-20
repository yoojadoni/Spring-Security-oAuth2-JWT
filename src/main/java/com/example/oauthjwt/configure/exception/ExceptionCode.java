package com.example.oauthjwt.configure.exception;

public enum ExceptionCode {

    UNKNOWN_ERROR(500)
    , WRONG_TYPE_TOKEN(400)
    , EXPIRED_TOKEN(400)
    , UNSUPPORTED_TOKEN(400)
    , ACCESS_DENIED(403)
    , PERMISSION_DENIED(403)
    , USER_NOT_FOUND(403)
    ;

    private final int value;

    ExceptionCode(int value) {
        this.value = value;
    }

    public int getCode(){
        return value;
    }

    public String getMessage(){
        return this.name();
    }


}
