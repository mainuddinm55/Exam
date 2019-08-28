package com.learner.task_01.utils;

import java.io.IOException;

class NoInternetException extends IOException {
    public NoInternetException(String message) {
        super(message);
    }
}
