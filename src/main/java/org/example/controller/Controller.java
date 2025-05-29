package org.example.controller;

import org.apache.log4j.Logger;
import org.example.entity.Port;
import org.example.entity.Ship;
import org.example.service.PortService;
import org.example.service.WarehouseService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Controller {
    private static final Port PORT = Port.getInstance();
    private static final Semaphore SEMAPHORE = new Semaphore(Port.getDockCount(), true);
    private static final ExecutorService executorService = Executors.newCachedThreadPool();
    private static final Logger LOGGER = Logger.getLogger(Controller.class);
    private static final PortService portService = new PortService(SEMAPHORE, PORT);

    public static void main(String[] args) {
        List<Callable<Integer>> callable = new ArrayList<>();
        for (int i = 0; i<15; i++) {
            callable.add(new Ship((int) (Math.random()*20+2), true, portService));
        }
        for (int i = 0; i<15; i++) {
            callable.add(new Ship((int) (Math.random()*20+2), false, portService));
        }
        try {
            WarehouseService warehouseService = new WarehouseService(PORT);
            Thread thread = new Thread(warehouseService);
            thread.setDaemon(true);
            thread.start();
            List<Future<Integer>> results = executorService.invokeAll(callable);
            for (Future<Integer> result : results) {
                try {
                    result.get();
                } catch (ExecutionException e) {
                    LOGGER.error("Error in task execution", e);
                }
            }
            executorService.shutdown();
        } catch (InterruptedException e) {
            LOGGER.error("error");
        }
    }
}