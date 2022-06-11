package com.Bsep.service.impl;

import com.Bsep.service.LoggerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerServiceImpl implements LoggerService {

    private final Logger logger;

    public LoggerServiceImpl(Class<?> parentClass) {
        this.logger = LogManager.getLogger(parentClass);
    }

    @Override
    public void loginSuccess(String email) {
        logger.info("Login successful: email : " + email);
    }

}
