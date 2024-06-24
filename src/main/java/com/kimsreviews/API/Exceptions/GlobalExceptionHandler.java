package com.kimsreviews.API.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundEXceptions.class)

    public ResponseEntity <ErrorObject> handleUserNotFoundException(UserNotFoundEXceptions ex,WebRequest request){
       ErrorObject errorObject=new ErrorObject();
       errorObject.setStatusCode(HttpStatus.NOT_FOUND.value());
       errorObject.setMessage(ex.getMessage());
       errorObject.setTimeStamp(new Date());

       return new ResponseEntity<ErrorObject>(errorObject,HttpStatus.NOT_FOUND);
    }
}
