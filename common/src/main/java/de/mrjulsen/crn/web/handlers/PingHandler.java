package de.mrjulsen.crn.web.handlers;

import com.sun.net.httpserver.HttpExchange;

import de.mrjulsen.crn.web.DLRestServer.DLRestManager;
import de.mrjulsen.crn.web.DLRestServer.QueryContext;

public class PingHandler extends DLRestManager {

    @Override
    public String path() {
        return "api/ping";
    }
    
    @Override
    public String run(HttpExchange exchange, QueryContext query) {
        return "Pong!";
    }
    
}
