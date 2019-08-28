package com.learner.task02.utils;

import java.io.IOException;

public class NoInternetException extends IOException {
    public NoInternetException(String message) {
        super(message);
    }
}
