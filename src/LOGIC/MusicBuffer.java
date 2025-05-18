/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LOGIC;

import DOMAIN.Song;
import java.util.LinkedList;

/**
 *
 * @author sinoe
 */
public class MusicBuffer {
    private final LinkedList<Song> buffer = new LinkedList<>();
    private final int MAX_SIZE = 20;

    public synchronized void addSong(Song song) throws InterruptedException {
        while (buffer.size() >= MAX_SIZE) wait();
        buffer.add(song);
        notifyAll();
    }

    public synchronized Song getSong() throws InterruptedException {
        while (buffer.isEmpty()) wait();
        Song s = buffer.removeFirst();
        notifyAll();
        return s;
    }

    public synchronized boolean isEmpty() {
        return buffer.isEmpty();
    }
}

