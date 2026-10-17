package server;

import io.javalin.*;
import service.*;

public class Server {

    private final Javalin javalin;
    //private final AdminService adminService;
    //private final UserService userService;

    public Server()
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
