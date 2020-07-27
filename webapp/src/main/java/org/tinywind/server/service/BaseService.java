package org.tinywind.server.service;

import org.tinywind.server.config.RequestGlobal;
import org.tinywind.server.config.RequestMessage;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseService {
    @Autowired
    protected RequestGlobal g;
    @Autowired
    protected RequestMessage message;
}
