package org.example.entity;

import java.util.concurrent.atomic.AtomicBoolean;


public class Dock {
    private final AtomicBoolean isBusy = new AtomicBoolean();

    public AtomicBoolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        isBusy.set(busy);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dock dock = (Dock) o;
        return isBusy.get() == dock.isBusy.get();
    }

    @Override
    public int hashCode() {
        return isBusy.hashCode();
    }
}