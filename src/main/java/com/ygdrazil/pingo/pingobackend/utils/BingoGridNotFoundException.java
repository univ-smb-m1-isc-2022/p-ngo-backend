package com.ygdrazil.pingo.pingobackend.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="No such Order")  // 404
public class BingoGridNotFoundException extends RuntimeException {
    public BingoGridNotFoundException(Long id) {
        super("No Bingo Grid found with id" + id);
    }
}