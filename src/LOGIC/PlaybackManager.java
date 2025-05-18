/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LOGIC;

import DOMAIN.Song;

import java.util.List;
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
    
    

    public Song getNextSong() {
        if (currentPlaylist == null || currentPlaylist.getSongs().isEmpty()) {
            return null;
        }

        if (mode == Mode.SEQUENTIAL) {
            Song song = currentPlaylist.getSongs().get(currentIndex);
            currentIndex = (currentIndex + 1) % currentPlaylist.getSongs().size();
            return song;
        } else { // SHUFFLE
            int index = random.nextInt(currentPlaylist.getSongs().size());
            return currentPlaylist.getSongs().get(index);
        }
    }

    public Song getPreviousSong() {
        if (currentPlaylist == null || currentPlaylist.getSongs().isEmpty()) {
            return null;
        }

        if (mode == Mode.SEQUENTIAL) {
            currentIndex = (currentIndex - 1 + currentPlaylist.getSongs().size()) % currentPlaylist.getSongs().size();
            return currentPlaylist.getSongs().get(currentIndex);
        } else { // SHUFFLE
            int index = random.nextInt(currentPlaylist.getSongs().size());
            return currentPlaylist.getSongs().get(index);
        }
    }
    
    public void playSongAtIndex(int index) {
    if (currentPlaylist == null || currentPlaylist.getSongs().isEmpty()) return;
    if (index < 0 || index >= currentPlaylist.getSongs().size()) return;
    currentIndex = index;
}

}

