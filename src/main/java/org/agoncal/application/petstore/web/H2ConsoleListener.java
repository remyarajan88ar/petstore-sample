package org.agoncal.application.petstore.web;

import org.h2.tools.Server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class H2ConsoleListener implements ServletContextListener {

    private Server server;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            // Start H2 web server for the console
            server = Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082").start();
            System.out.println("H2 Web Console started successfully at http://localhost:8082");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            if (server != null) {
                server.stop();
                System.out.println("H2 Web Console stopped.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
