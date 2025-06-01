/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LOGIC;

import DOMAIN.Song;

import java.util.Random;

/**
 *
 * @author sinoe
 */
public class PlaybackManager {

    public enum Mode {
        SEQUENTIAL,
        SHUFFLE
    }

    private Playlist currentPlaylist;
    private Mode mode = Mode.SEQUENTIAL;
    private int currentIndex = 0;
    private final Random random = new Random();

    public PlaybackManager() {}

    public void setPlaylist(Playlist playlist) {
        this.currentPlaylist = playlist;
        this.currentIndex = 0;
    }

    public Playlist getCurrentPlaylist() {
        return currentPlaylist;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Mode getMode() {
        return mode;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public Song getCurrentSong() {
        if (currentPlaylist == null || currentPlaylist.getSongs().isEmpty()) {
            return null;
        }
        return currentPlaylist.getSongs().get(currentIndex);
    }

    public void next() {
        if (currentPlaylist == null || currentPlaylist.getSongs().isEmpty()) return;
        if (mode == Mode.SEQUENTIAL) {
            currentIndex = (currentIndex + 1) % currentPlaylist.getSongs().size();
        } else {
            currentIndex = random.nextInt(currentPlaylist.getSongs().size());
        }
    }

    public void previous() {
        if (currentPlaylist == null || currentPlaylist.getSongs().isEmpty()) return;
        if (mode == Mode.SEQUENTIAL) {
            currentIndex = (currentIndex - 1 + currentPlaylist.getSongs().size()) % currentPlaylist.getSongs().size();
        } else {
            currentIndex = random.nextInt(currentPlaylist.getSongs().size());
        }
    }

    public void playSongAtIndex(int index) {
        if (currentPlaylist == null || currentPlaylist.getSongs().isEmpty()) return;
        if (index < 0 || index >= currentPlaylist.getSongs().size()) return;
        currentIndex = index;
    }
}
