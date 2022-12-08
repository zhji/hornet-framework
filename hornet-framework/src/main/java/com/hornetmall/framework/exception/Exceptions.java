package com.hornetmall.framework.exception;

public class Exceptions {

    public static void noContent(Object id,Class entityClass){
        throw new NotContentException();
    }
}
