package dev.hideftbanana.netcafejavafxapp.services.cacheservices;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class SimpleCache<K, V> {
    protected final int maxSize;
    protected final Map<K, V> cache;

    public SimpleCache(int maxSize) {
        this.maxSize = maxSize;
        this.cache = new LinkedHashMap<K, V>(maxSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > maxSize;
            }
        };
    }

    abstract void put(K key, V value);

    abstract V get(K key);
}
