package com.dobbinsoft.netting.base.ext;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteHashMap<K, V> extends HashMap<K, V> {

    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    @Override
    public boolean containsKey(Object key) {
        readWriteLock.readLock().lock();
        try {
            return super.containsKey(key);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public boolean containsValue(Object value) {
        readWriteLock.readLock().lock();
        try {
            return super.containsValue(value);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public V get(Object key) {
        readWriteLock.readLock().lock();
        try {
            return super.get(key);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public V put(K key, V value) {
        readWriteLock.writeLock().lock();
        try {
            return super.put(key, value);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public V remove(Object key) {
        readWriteLock.writeLock().lock();
        try {
            return super.remove(key);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        readWriteLock.writeLock().lock();
        try {
            super.putAll(m);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public void clear() {
        readWriteLock.writeLock().lock();
        try {
            super.clear();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public Set<K> keySet() {
        readWriteLock.readLock().lock();
        try {
            return super.keySet();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Collection<V> values() {
        readWriteLock.readLock().lock();
        try {
            return super.values();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        readWriteLock.readLock().lock();
        try {
            return super.entrySet();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }
}
