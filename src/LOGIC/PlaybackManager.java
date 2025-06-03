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
        this.currentIndex = 0;  // Solo reinicia índice al cargar nueva playlist
        System.out.println("[PlaybackManager] setPlaylist: " + playlist.getName() + ", índice reiniciado a 0");
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
        if (currentPlaylist == null || currentPlaylist.getSongs().isEmpty()) return;
        if (currentIndex < 0 || currentIndex >= currentPlaylist.getSongs().size()) return;
        this.currentIndex = currentIndex;
        System.out.println("[PlaybackManager] setCurrentIndex: " + currentIndex);
    }

    public Song getCurrentSong() {
        if (currentPlaylist == null || currentPlaylist.getSongs().isEmpty()) {
            return null;
        }
        return currentPlaylist.getSongs().get(currentIndex);
    }

    public void next() {
        if (currentPlaylist == null || currentPlaylist.getSongs().isEmpty()) return;
        System.out.println("[PlaybackManager] next() antes: currentIndex = " + currentIndex);
        if (mode == Mode.SEQUENTIAL) {
            currentIndex = (currentIndex + 1) % currentPlaylist.getSongs().size();
        } else {
            currentIndex = random.nextInt(currentPlaylist.getSongs().size());
        }
        System.out.println("[PlaybackManager] next() después: currentIndex = " + currentIndex);
    }

    public void previous() {
        if (currentPlaylist == null || currentPlaylist.getSongs().isEmpty()) return;
        System.out.println("[PlaybackManager] previous() antes: currentIndex = " + currentIndex);
        if (mode == Mode.SEQUENTIAL) {
            currentIndex = (currentIndex - 1 + currentPlaylist.getSongs().size()) % currentPlaylist.getSongs().size();
        } else {
            currentIndex = random.nextInt(currentPlaylist.getSongs().size());
        }
        System.out.println("[PlaybackManager] previous() después: currentIndex = " + currentIndex);
    }

    public void playSongAtIndex(int index) {
        if (currentPlaylist == null || currentPlaylist.getSongs().isEmpty()) return;
        if (index < 0 || index >= currentPlaylist.getSongs().size()) return;
        currentIndex = index;
        System.out.println("[PlaybackManager] playSongAtIndex: " + index);
    }
}
