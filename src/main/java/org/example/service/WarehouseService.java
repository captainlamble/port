package org.example.service;

import org.apache.log4j.Logger;
import org.example.entity.Port;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class WarehouseService implements Runnable {
    private final ReentrantLock lock = new ReentrantLock();
    private final Port port;
    private static final Logger LOGGER = Logger.getLogger(WarehouseService.class);
    private volatile boolean stopRequested = false;


    public WarehouseService (Port port) {
        this.port = port;
    }

    public void stopService() {
        stopRequested = true;
    }

    @Override
    public void run() {
        while (!stopRequested) {
            lock.lock();
            try {
                if (port.getStorage().get() < (Port.getStorageCapacity() / 2 + 1)) {
                    port.setStorage((Port.getStorageCapacity() / 10 * 9));
                } else if (port.getStorage().get() > (Port.getStorageCapacity() / 5) * 4) {
                    port.setStorage(Port.getStorageCapacity() / 10);
                }
            } finally {
                lock.unlock();
            }
            try {
                TimeUnit.MILLISECONDS.sleep(5);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.error("demon error");
            }
        }
    }
}
