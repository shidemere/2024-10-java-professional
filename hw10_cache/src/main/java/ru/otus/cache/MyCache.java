package ru.otus.cache;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class MyCache<K, V> implements HwCache<K, V> {
    // Надо реализовать эти методы
    private final Map<K, V> cache;
    private final CopyOnWriteArrayList<HwListener<K, V>> listeners = new CopyOnWriteArrayList<>();

    public MyCache() {
        this.cache = new WeakHashMap<>();
    }

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
        notifyListeners(key, value, "put");
    }

    @Override
    public void remove(K key) {
        V removed = cache.remove(key);
        notifyListeners(key, removed, "remove");
    }

    @Override
    public V get(K key) {
        return cache.get(key);
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(K key, V value, String action) {
        for (HwListener<K, V> listener : listeners) {
            listener.notify(key, value, action);
        }
    }
}
