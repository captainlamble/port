package org.example.entity;

import org.example.service.PortService;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Ship implements Callable<Integer>{
    private final AtomicInteger cargo = new AtomicInteger();
    private final AtomicBoolean isFull = new AtomicBoolean();
    private final AtomicInteger dockNumber = new AtomicInteger();
    private final PortService portService;

    public Ship(int cargo, boolean isFull, PortService portService) {
        this.cargo.set(cargo);
        this.isFull.set(isFull);
        this.portService = portService;
    }

    @Override
    public Integer call() throws Exception {
        int i = cargo.get();
        portService.takeShip(this);
        portService.leaveShip(this);
        return i;
    }

    public AtomicInteger getCargo() {
        return cargo;
    }

    public AtomicBoolean getIsFull() {
        return isFull;
    }

    public AtomicInteger getDockNumber() {
        return dockNumber;
    }

    public void setDockNumber(int dockNumber) {
        this.dockNumber.set(dockNumber);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ship)) return false;
        Ship ship = (Ship) o;
        if (!cargo.equals(ship.cargo)) return false;
        if (!isFull.equals(ship.isFull)) return false;
        if (!dockNumber.equals(ship.dockNumber)) return false;
        return portService != null ? portService.equals(ship.portService) : ship.portService == null;
    }

    @Override
    public int hashCode() {
        int result = cargo.hashCode();
        result = 31 * result + isFull.hashCode();
        result = 31 * result + dockNumber.hashCode();
        result = 31 * result + (portService != null ? portService.hashCode() : 0);
        return result;
    }
}