/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DOMAIN;

import java.awt.Image;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 *
 * @author sinoe
 */
public class ImageCache {
    private static final int MAX_ENTRIES = 100;
    private static final Map<String, Image> cache = new LinkedHashMap<>(MAX_ENTRIES, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Image> eldest) {
            return size() > MAX_ENTRIES;
        }
    };

    public static Image get(String path) {
        return cache.get(path);
    }

    public static void put(String path, Image img) {
        if (path != null && img != null) {
            cache.put(path, img);
        }
    }

    public static void clear() {
        cache.clear();
    }
}

