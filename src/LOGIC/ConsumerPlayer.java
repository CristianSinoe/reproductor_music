/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LOGIC;

import DOMAIN.Song;
import javazoom.jl.player.Player;
import java.io.FileInputStream;
import java.io.IOException;

/**
 *
 * @author sinoe
 */
public class ConsumerPlayer extends Thread {

    private final PlaybackManager playbackManager;
    private NowPlayingCallback callback;

    private volatile boolean paused = false;
    private volatile boolean stopped = false;
    private volatile boolean songChanged = false;

    private Player currentPlayer = null;
    private FileInputStream currentStream = null;
    private final Object playerLock = new Object();

    private Thread playThread;

    // Para controlar progreso basado en tiempo
    private long startTime = 0;
    private long pausedTimeAccumulated = 0;
    private long pauseStartTime = 0;
    private int durationMillis = 0; // Debes establecer la duración al cargar la canción

    public interface NowPlayingCallback {
        void onSongChanged(String songName);
    }

    public ConsumerPlayer(PlaybackManager playbackManager) {
        this.playbackManager = playbackManager;
    }

    public void setNowPlayingCallback(NowPlayingCallback callback) {
        this.callback = callback;
    }

    public void setDuration(int millis) {
        this.durationMillis = millis;
    }

    public synchronized int getProgressPercent() {
        if (durationMillis == 0) return 0;
        long played;
        if (paused) {
            played = pauseStartTime - startTime - pausedTimeAccumulated;
        } else {
            long now = System.currentTimeMillis();
            played = now - startTime - pausedTimeAccumulated;
        }
        int percent = (int) ((played * 100) / durationMillis);
        return Math.min(percent, 100);
    }

    public synchronized void pause() {
        if (!paused) {
            paused = true;
            pauseStartTime = System.currentTimeMillis();
            synchronized (playerLock) {
                if (currentPlayer != null) {
                    currentPlayer.close();  // Pausa efectiva (detener la reproducción)
                }
            }
        }
    }

    public synchronized void play() {
        if (paused) {
            paused = false;
            pausedTimeAccumulated += System.currentTimeMillis() - pauseStartTime;
            songChanged = true;  // Forzar reinicio de reproducción
            notify();
            interrupt();
        }
    }

    public synchronized void changeSong() {
        songChanged = true;
        paused = false;
        pausedTimeAccumulated = 0;
        notify();
        interrupt();
    }

    public synchronized void stopPlayback() {
        stopped = true;
        songChanged = true;
        notify();
        interrupt();
    }

    // NUEVOS métodos públicos para que NowPlayingWindow pueda controlar la reproducción
    public Song getCurrentSong() {
        return playbackManager.getCurrentSong();
    }

    public void next() {
        playbackManager.next();
    }

    public void previous() {
        playbackManager.previous();
    }

    @Override
    public void run() {
        while (!stopped) {
            try {
                synchronized (this) {
                    while (paused && !stopped) wait();
                    if (stopped) break;
                }

                Song song;
                synchronized (this) {
                    if (songChanged) {
                        song = playbackManager.getCurrentSong();
                        songChanged = false;
                    } else {
                        song = playbackManager.getCurrentSong();
                    }
                }

                if (song == null) {
                    Thread.sleep(500);
                    continue;
                }

                if (callback != null) {
                    javax.swing.SwingUtilities.invokeLater(() -> callback.onSongChanged(song.getName()));
                }

                // Aquí debes establecer la duración de la canción si tienes ese dato
                if (song.getDurationMillis() > 0) {
                    setDuration(song.getDurationMillis());
                } else {
                    setDuration(0); // Sin dato
                }

                startTime = System.currentTimeMillis();
                pausedTimeAccumulated = 0;

                synchronized (playerLock) {
                    if (currentPlayer != null) {
                        currentPlayer.close();
                        currentPlayer = null;
                    }
                    if (currentStream != null) {
                        try {
                            currentStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        currentStream = null;
                    }
                    currentStream = new FileInputStream(song.getFile());
                    currentPlayer = new Player(currentStream);
                }

                Player playerToPlay;
                synchronized (playerLock) {
                    playerToPlay = currentPlayer;
                }

                playThread = new Thread(() -> {
                    try {
                        if (playerToPlay != null) {
                            playerToPlay.play();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                playThread.start();

                while (playThread.isAlive()) {
                    if (songChanged || stopped || paused) {
                        synchronized (playerLock) {
                            if (playerToPlay != null) {
                                playerToPlay.close();
                            }
                        }
                        playThread.interrupt();
                        break;
                    }
                    Thread.sleep(100);
                }

                synchronized (playerLock) {
                    if (currentStream != null) {
                        try {
                            currentStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        currentStream = null;
                    }
                    currentPlayer = null;
                }

                synchronized (this) {
                    if (!songChanged && !stopped && !paused) {
                        wait();
                    }
                }

            } catch (InterruptedException e) {
                // Ignorar interrupciones
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        synchronized (playerLock) {
            if (currentPlayer != null) {
                currentPlayer.close();
                currentPlayer = null;
            }
            if (currentStream != null) {
                try {
                    currentStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                currentStream = null;
            }
        }
    }
}
