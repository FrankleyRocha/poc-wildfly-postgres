package org.wildfly.examples;

import jakarta.websocket.server.PathParam;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/hello")
public class BookStoreApplication extends Application {

    @GET
    public String sayHello(@PathParam(value = "name") String name){
        return "Hello %s".formatted(name);
    }

}
