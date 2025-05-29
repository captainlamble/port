package org.example.service;

import org.apache.log4j.Logger;
import org.example.entity.Port;
import org.example.entity.Ship;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class PortService {
    private final Semaphore semaphore;
    private final ReentrantLock lock = new ReentrantLock();
    private final Port port;

    private static final Logger LOGGER = Logger.getLogger(PortService.class);

    public PortService(Semaphore semaphore, Port port) {
        this.semaphore = semaphore;
        this.port = port;
    }

    public void takeShip(Ship ship) {
        try {
            semaphore.acquire();
            LOGGER.info("Ship " + Thread.currentThread().getName() + " is waiting for a dock...");
        } catch (InterruptedException e) {
            LOGGER.error("Semaphore acquire interrupted for ship: " + Thread.currentThread().getName(), e);
            Thread.currentThread().interrupt();
        }

        lock.lock();
        try {
            for (int i = 0; i < port.getDocks().length; i++) {
                if (!port.getDocks()[i].isBusy().get()) {
                    port.getDocks()[i].setBusy(true);
                    ship.setDockNumber(i);

                    LOGGER.info("Ship " + Thread.currentThread().getName()
                            + " received dock " + i);
                    break;
                }
            }
        } finally {
            lock.unlock();
        }

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            LOGGER.error("Error during docking for ship: " + Thread.currentThread().getName(), e);
            Thread.currentThread().interrupt();
        }
    }

    public void leaveShip(Ship ship) {
        lock.lock();
        try {
            int dockNumber = ship.getDockNumber().get();
            boolean isFull = ship.getIsFull().get();

            LOGGER.info("Ship " + Thread.currentThread().getName()
                    + " is leaving dock " + dockNumber + ". Full cargo: " + isFull);

            if (isFull) {
                unloadShip(ship, dockNumber);
            } else {
                loadShip(ship, dockNumber);
            }

        } finally {
            lock.unlock();
        }

        semaphore.release();
    }

    private void unloadShip(Ship ship, int dockNumber) {
        while (true) {
            lock.lock();
            try {
                if ((Port.getStorageCapacity() - port.getStorage().get()) >= ship.getCargo().get()) {

                    port.setStorage(port.getStorage().get() + ship.getCargo().get());
                    ship.getCargo().set(0);

                    LOGGER.info("Ship " + Thread.currentThread().getName()
                            + " unloaded cargo at dock " + dockNumber);
                    port.getDocks()[dockNumber].setBusy(false);
                    break;
                } else {
                    LOGGER.info("Warehouse full, waiting to unload: " + Thread.currentThread().getName());
                }
            } finally {
                lock.unlock();
            }

            waitBeforeRetry();
        }
    }

    private void loadShip(Ship ship, int dockNumber) {
        while (true) {
            lock.lock();
            try {
                if (port.getStorage().get() >= ship.getCargo().get()) {
                    port.setStorage(port.getStorage().get() - ship.getCargo().get());
                    ship.getCargo().set(0);

                    LOGGER.info("Ship " + Thread.currentThread().getName()
                            + " loaded cargo at dock " + dockNumber);
                    port.getDocks()[dockNumber].setBusy(false);
                    break;
                } else {
                    LOGGER.info("Not enough items in warehouse, waiting to load: " + Thread.currentThread().getName());
                }
            } finally {
                lock.unlock();
            }

            waitBeforeRetry();
        }
    }

    private void waitBeforeRetry() {
        try {
            TimeUnit.MILLISECONDS.sleep(20);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.error("Error during retry wait.", e);
        }
    }
}