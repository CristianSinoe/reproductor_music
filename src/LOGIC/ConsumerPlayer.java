/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LOGIC;

import DOMAIN.Song;
import javazoom.jl.player.Player;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author sinoe
 */
public class ConsumerPlayer extends Thread {

    private PlaybackManager playbackManager;
    private NowPlayingCallback callback;

    private volatile boolean paused = false;
    private volatile boolean stopped = false;
    private volatile boolean songChanged = false;

    private Player currentPlayer = null;
    private FileInputStream currentStream = null;
    private final Object playerLock = new Object();

    public interface NowPlayingCallback {
        void onSongChanged(String songName);
    }

    public ConsumerPlayer(PlaybackManager playbackManager) {
        this.playbackManager = playbackManager;
    }

    public void setNowPlayingCallback(NowPlayingCallback callback) {
        this.callback = callback;
    }

    public synchronized void pause() {
        paused = true;
    }

    public synchronized void play() {
        paused = false;
        notify();
    }

    public void stopPlayback() {
    stopped = true;
    songChanged = true;
    interrupt(); // Interrumpe si está en wait()
    // Dejamos que run() limpie los recursos
}

    public synchronized void changeSong() {
        songChanged = true;
        notify();
        interrupt(); // Interrumpe si está en wait()
        // Dejamos que run() maneje la limpieza y la reproducción
    }

    @Override
public void run() {
    while (!stopped) {
        try {
            synchronized (this) {
                while (paused && !stopped) wait();
                if (stopped) break;
            }

            Song song = playbackManager.getNextSong();
            if (song == null) {
                Thread.sleep(500);
                continue;
            }

            if (callback != null) {
                javax.swing.SwingUtilities.invokeLater(() -> callback.onSongChanged(song.getName()));
            }

            // Limpieza previa
            synchronized (playerLock) {
                if (currentStream != null) {
                    try { currentStream.close(); } catch (IOException e) { e.printStackTrace(); }
                    currentStream = null;
                }
                if (currentPlayer != null) {
                    currentPlayer.close();
                    currentPlayer = null;
                }

                // Inicialización para la nueva canción
                currentStream = new FileInputStream(song.getFile());
                currentPlayer = new Player(currentStream);
            }

            // Reproduce la canción (bloqueante hasta que termine)
            currentPlayer.play();

            // Limpieza después de la reproducción
            synchronized (playerLock) {
                if (currentStream != null) {
                    try { currentStream.close(); } catch (IOException e) { e.printStackTrace(); }
                    currentStream = null;
                }
                currentPlayer = null;
            }

            synchronized (this) {
                if (!songChanged && !stopped) {
                    wait();  // Espera hasta que se cambie la canción o se detenga
                }
                songChanged = false;
            }

        } catch (InterruptedException e) {
            // Permite la interrupción para cambiar o detener la canción
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Limpieza final de recursos
    synchronized (playerLock) {
        if (currentPlayer != null) {
            currentPlayer.close();
            currentPlayer = null;
        }
        if (currentStream != null) {
            try { currentStream.close(); } catch (IOException e) { e.printStackTrace(); }
            currentStream = null;
        }
    }
}
}
