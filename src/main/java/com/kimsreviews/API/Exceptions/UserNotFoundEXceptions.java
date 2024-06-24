package com.kimsreviews.API.Exceptions;

import aj.org.objectweb.asm.commons.SerialVersionUIDAdder;

public class UserNotFoundEXceptions extends RuntimeException{

    public static final long SerialVersionUIDA=1;

    public UserNotFoundEXceptions (String message){
       super(message);

    }


}
