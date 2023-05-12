package com.example.javafxjdbcmvc.model.exceptions;

import java.util.HashMap;
import java.util.Map;

public class ValidationException extends RuntimeException {
    //Essa exceção tem que carregar a mensagem de erro específica do formulário

    //Primeira String é o campo e o segundo é a mensagem de erro
    private Map<String, String> errors = new HashMap<>();
    public ValidationException(String msg){
        super(msg);
    }

    public Map<String, String> getErrors(){
        return errors;
    }

    public void addErrors(String fieldName, String message){
        errors.put(fieldName, message);
    }
}
