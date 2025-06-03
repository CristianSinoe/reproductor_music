/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

import javax.swing.*;
import java.awt.*;
import LOGIC.*;
import LOGIC.MusicBuffer;
import LOGIC.ConsumerPlayer;
import DOMAIN.Song;
import java.util.stream.Collectors;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 *
 * @author sinoe
 */
public class MainWindow extends JFrame {
    private final PlayerPanel playerPanel = new PlayerPanel();

    private MusicManager musicManager;
    private MusicBuffer musicBuffer;
    private PlaybackManager playbackManager;
    private ConsumerPlayer consumerPlayer;
    private PlaylistManager playlistManager;
    private boolean ignoreComboEvents = false;

    private JComboBox<String> playlistsCombo = new JComboBox<>();
//    private JButton btnCreatePlaylist = new JButton("NUEVA PLAYLIST");
//    private JButton btnDeletePlaylist = new JButton("ELIMINAR PLAYLIST");
//    private JButton btnRenamePlaylist = new JButton("RENOMBRAR PLAYLIST");
//    private JButton btnAddSongToPlaylist = new JButton("AÑADIR CANCIONES A LA PLAYLIST");

    private Playlist activePlaylist;

    // Timer para actualizar barra de progreso
    private Timer progressTimer;

    public MainWindow() {
        setTitle("REPRODUCTOR DE MUSICA");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

//        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//        topPanel.add(new JLabel("PLAYLIST:"));
//        topPanel.add(playlistsCombo);
//        topPanel.add(btnCreatePlaylist);
//        topPanel.add(btnDeletePlaylist);
//        topPanel.add(btnRenamePlaylist);
//        topPanel.add(btnAddSongToPlaylist);

//        add(topPanel, BorderLayout.NORTH);
        add(playerPanel, BorderLayout.CENTER);

        musicManager = new MusicManager();
        musicBuffer = new MusicBuffer();
        playbackManager = new PlaybackManager();

        File musicFolder = new File("C:\\Users\\sinoe\\Music");
        File playlistsDir = new File(System.getProperty("user.home"), "MyMusicPlayerPlaylists");
        playlistManager = new PlaylistManager(playlistsDir);

        loadSongsAndPlaylists(musicFolder);
        setupListeners();
        setupProgressTimer();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                dispose();
                System.exit(0);
            }
        });

        setVisible(true);
    }

    private void setupProgressTimer() {
        progressTimer = new Timer(200, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (consumerPlayer != null && !consumerPlayer.isInterrupted()) {
                    int progress = consumerPlayer.getProgressPercent();
                    playerPanel.setProgress(progress);
                }
            }
        });
        progressTimer.start();
    }

    private void loadSongsAndPlaylists(File musicFolder) {
        SwingWorker<Void, Song> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                File[] files = musicFolder.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.getName().toLowerCase().endsWith(".mp3")) {
                            Song song = musicManager.loadSong(file);
                            if (song != null) {
                                publish(song);
                                Thread.sleep(10);
                            }
                        }
                    }
                }
                return null;
            }

            @Override
            protected void process(List<Song> chunks) {
                playerPanel.addSongs(chunks);
                if (activePlaylist == null) {
                    Playlist tempPlaylist = new Playlist("CARGANDO...");
                    tempPlaylist.getSongs().addAll(playerPanel.getSongs());
                    setActivePlaylist(tempPlaylist);
                } else {
                    activePlaylist.getSongs().addAll(chunks);
                }
            }

            @Override
            protected void done() {
                try {
                    loadPlaylists();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(MainWindow.this, "ERROR CARGANDO PLAYLIST: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void loadPlaylists() throws IOException {
        List<Playlist> playlists = playlistManager.loadAll().stream().filter(p -> p != null).collect(Collectors.toList());

        if (playlists.isEmpty()) {
            Playlist allSongs = new Playlist("TODAS LAS CANCIONES");
            allSongs.getSongs().addAll(musicManager.getAllSongs());
            playlistManager.savePlaylist(allSongs);
            playlists.add(allSongs);
        }

        playlistsCombo.removeAllItems();
        for (Playlist p : playlists) {
            playlistsCombo.addItem(p.getName());
        }

        ignoreComboEvents = true;
        playlistsCombo.setSelectedItem(playlists.get(0).getName());
        ignoreComboEvents = false;
    }

    private void setActivePlaylist(Playlist playlist) {
        activePlaylist = playlist;
        playbackManager.setPlaylist(activePlaylist);
        playbackManager.setCurrentIndex(0);

        System.out.println("PLAY LIST ACTIVA ESTABLECIDA: " + playlist.getName());
        System.out.println("CANCIONES CARGADAS EN LA PLAYLIST: " + playlist.getSongs().size());
        System.out.println("INDICE INICIAL: " + playbackManager.getCurrentIndex());

        playerPanel.setSongs(activePlaylist.getSongs());

        if (consumerPlayer != null) {
            consumerPlayer.stopPlayback();
            consumerPlayer.interrupt();
            try {
                consumerPlayer.join(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        consumerPlayer = new ConsumerPlayer(playbackManager);
        consumerPlayer.setNowPlayingCallback(playerPanel::setNowPlaying);
        consumerPlayer.start();

        updateUISelection();
    }

    private void updateUISelection() {
        int currentIndex = playbackManager.getCurrentIndex();
        playerPanel.setSelectedSongIndex(currentIndex);
    }

    private void setupListeners() {
        playlistsCombo.addActionListener(e -> {
            if (ignoreComboEvents) return;

            String selected = (String) playlistsCombo.getSelectedItem();
            if (selected != null) {
                try {
                    List<Playlist> playlists = playlistManager.loadAll();
                    playlists.stream()
                        .filter(p -> p.getName().equals(selected))
                        .findFirst()
                        .ifPresent(this::setActivePlaylist);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "ERROR CARGANDO PLAYLIST: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        playerPanel.setCoverClickListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
        if (consumerPlayer == null) return;
        Song currentSong = playbackManager.getCurrentSong();
        if (currentSong != null) {
            NowPlayingWindow nowPlayingWindow = new NowPlayingWindow(MainWindow.this, currentSong, consumerPlayer);
            nowPlayingWindow.setVisible(true);
        }
            }
        });


//        btnCreatePlaylist.addActionListener(e -> {
//            String name = JOptionPane.showInputDialog(this, "NOMBRE DE NUEVA PLAYLIST:");
//            if (name != null && !name.trim().isEmpty()) {
//                Playlist newPlaylist = new Playlist(name.trim());
//                try {
//                    playlistManager.savePlaylist(newPlaylist);
//                    playlistsCombo.addItem(newPlaylist.getName());
//                    playlistsCombo.setSelectedItem(newPlaylist.getName());
//                } catch (IOException ex) {
//                    JOptionPane.showMessageDialog(this, "ERROR CREANDO PLAYLIST: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
//                }
//            }
//        });
//
//        btnDeletePlaylist.addActionListener(e -> {
//            if (activePlaylist == null || activePlaylist.getName().equals("TODAS LAS CANCIONES")) {
//                JOptionPane.showMessageDialog(this, "NO SE PUEDE ELIMINAR ESTA PLAYLIST.", "ATENCIÓN", JOptionPane.WARNING_MESSAGE);
//                return;
//            }
//            int res = JOptionPane.showConfirmDialog(this, "¿ELIMINAR PLAYLIST " + activePlaylist.getName() + "?", "CONFIRMAR", JOptionPane.YES_NO_OPTION);
//            if (res == JOptionPane.YES_OPTION) {
//                playlistManager.deletePlaylist(activePlaylist);
//                playlistsCombo.removeItem(activePlaylist.getName());
//                if (playlistsCombo.getItemCount() > 0) {
//                    playlistsCombo.setSelectedIndex(0);
//                }
//            }
//        });

        playerPanel.setPlayAction(e -> {
            System.out.println("PLAY PRESIONADO");
            if (consumerPlayer != null) consumerPlayer.play();
        });

        playerPanel.setPauseAction(e -> {
            System.out.println("PAUSA PRESIONADO");
            if (consumerPlayer != null) consumerPlayer.pause();
        });

        playerPanel.setNextAction(e -> {
            System.out.println("SIGUIENTE PRESIONADO");
            if (consumerPlayer != null) {
                playbackManager.next();
                consumerPlayer.changeSong();
                updateUISelection();
            }
        });

        playerPanel.setPrevAction(e -> {
            System.out.println("ANTERIOR PRESIONADO");
            if (consumerPlayer != null) {
                playbackManager.previous();
                consumerPlayer.changeSong();
                updateUISelection();
            }
        });

        playerPanel.setPlaySongListener(index -> {
            System.out.println("CANCION SELECCIONADA CON INDICE: " + index);
            if (consumerPlayer != null) {
                playbackManager.playSongAtIndex(index);
                consumerPlayer.changeSong();
                updateUISelection();
            }
        });
    }

    @Override
    public void dispose() {
        System.out.println("CERRANDO APLICACIÓN...");
        if (consumerPlayer != null) {
            consumerPlayer.stopPlayback();
            consumerPlayer.interrupt();
            try {
                consumerPlayer.join(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(progressTimer != null) {
            progressTimer.stop();
        }
        musicManager.shutdown();
        super.dispose();
    }
}
