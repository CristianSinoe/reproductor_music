/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LOGIC;

import DOMAIN.Song;

/**
 *
 * @author sinoe
 */
public class Producer extends Thread {
    private final MusicBuffer buffer;
    private final MusicManager manager;

    public Producer(MusicBuffer buffer, MusicManager manager) {
        this.buffer = buffer;
        this.manager = manager;
    }

    @Override
    public void run() {
        try {
            for (Song s : manager.getAllSongs()) {
                buffer.addSong(s);
                System.out.println("AGREGANDO AL BUFFER: " + s.getName());
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

