package org.example.entity;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class Port {
    private static Port instance;
    private final Dock[] docks;
    private static final int DOCK_COUNT = 5;
    private final AtomicInteger storage;
    private static final int STORAGE_CAPACITY = 50;
    private static final ReentrantLock lock = new ReentrantLock();

    private Port() {
        this.docks = new Dock[DOCK_COUNT];
        for (int i = 0;i<docks.length;i++){
            docks[i]= new Dock();
            docks[i].setBusy(false);
        }
        this.storage = new AtomicInteger();
        storage.set(0);
    }

    public static Port getInstance() {
        if (instance == null) {
            lock.lock();
            try {
                if (instance == null)
                    instance = new Port();
                return instance;
            }
            finally {
                lock.unlock();
            }
        }
        return instance;
    }

    public Dock[] getDocks() {
        return docks;
    }

    public AtomicInteger getStorage() {
        return storage;
    }

    public void setStorage(int storage) {
        this.storage.set(storage);
    }

    public static int getStorageCapacity() {
        return STORAGE_CAPACITY;
    }

    public static int getDockCount() {
        return DOCK_COUNT;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Port)) return false;
        Port port = (Port) o;
        if (!(port.docks.length==docks.length)) return false;
        for (int i=0;i<docks.length;i++) {
            if (!port.docks[i].equals(docks[i])) return false;
        }
        return storage.equals(port.storage);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(docks);
        result = 31 * result + storage.hashCode();
        return result;
    }
}
