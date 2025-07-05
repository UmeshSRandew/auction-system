package lk.jiat.ee.auction.web;


import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lk.jiat.ee.auction.db.DatabaseUtil;

import java.util.logging.Logger;

@WebListener
public class AppServletContextListener implements ServletContextListener {
    private static final Logger LOGGER = Logger.getLogger(AppServletContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOGGER.info("Application Starting - Initializing Database...");
        DatabaseUtil.initDb(); // Call the initDb method
        LOGGER.info("Database initialization attempt complete from ServletContextListener.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.info("Application Shutting Down.");
    }
}
