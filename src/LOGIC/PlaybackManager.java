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
        System.out.println("[PLAY BACKMANAGER] SETPLAYLIST: " + playlist.getName() + ", INDICE REINICIADO A 0");
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
        System.out.println("[PLAY BACKMANAGER] SETCURRENTINDEX: " + currentIndex);
    }

    public Song getCurrentSong() {
        if (currentPlaylist == null || currentPlaylist.getSongs().isEmpty()) {
            return null;
        }
        return currentPlaylist.getSongs().get(currentIndex);
    }

    public void next() {
        if (currentPlaylist == null || currentPlaylist.getSongs().isEmpty()) return;
        System.out.println("[PLAY BACKMANAGER] NEXT() ANTES: CURRENTINDEX = " + currentIndex);
        if (mode == Mode.SEQUENTIAL) {
            currentIndex = (currentIndex + 1) % currentPlaylist.getSongs().size();
        } else {
            currentIndex = random.nextInt(currentPlaylist.getSongs().size());
        }
        System.out.println("[PLAY BACKMANAGER] NEXT() DESPUES: CURRENTINDEX = " + currentIndex);
    }

    public void previous() {
        if (currentPlaylist == null || currentPlaylist.getSongs().isEmpty()) return;
        System.out.println("[PLAY BACKMANAGER] PREVIOUS() ANTES: CURRENTEINDEX = " + currentIndex);
        if (mode == Mode.SEQUENTIAL) {
            currentIndex = (currentIndex - 1 + currentPlaylist.getSongs().size()) % currentPlaylist.getSongs().size();
        } else {
            currentIndex = random.nextInt(currentPlaylist.getSongs().size());
        }
        System.out.println("[PLAY BACKMANAGER] PREVIOUS() DESPUES: CURRENTEINDEX = " + currentIndex);
    }

    public void playSongAtIndex(int index) {
        if (currentPlaylist == null || currentPlaylist.getSongs().isEmpty()) return;
        if (index < 0 || index >= currentPlaylist.getSongs().size()) return;
        currentIndex = index;
        System.out.println("[PLAY BACKMANAGER] PLAYSONGATINDEX: " + index);
    }
}
