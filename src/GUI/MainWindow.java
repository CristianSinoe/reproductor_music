/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

import LOGIC.MusicBuffer;
import LOGIC.ConsumerPlayer;
import javax.swing.*;
import java.awt.*;
import DOMAIN.Song;
import LOGIC.*;
import java.util.stream.Collectors;
import java.io.File;
import java.io.IOException;
import java.util.List;



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
    private JButton btnCreatePlaylist = new JButton("NUEVA PLAYLIST");
    private JButton btnDeletePlaylist = new JButton("ELIMINAR PLAYLIST");
    private JButton btnRenamePlaylist = new JButton("RENOMBRAR PLAYLIST");
    private JButton btnAddSongToPlaylist = new JButton("AÑADIR CANCIONES A LA PLAYLIST");

    private Playlist activePlaylist;

    public MainWindow() {
        setTitle("🎵 REPRODUCTOR DE MUSICA");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("PLAYLIST:"));
        topPanel.add(playlistsCombo);
        topPanel.add(btnCreatePlaylist);
        topPanel.add(btnDeletePlaylist);
        topPanel.add(btnRenamePlaylist);
        topPanel.add(btnAddSongToPlaylist);

        add(topPanel, BorderLayout.NORTH);
        add(playerPanel, BorderLayout.CENTER);

        musicManager = new MusicManager();
        musicBuffer = new MusicBuffer();
        playbackManager = new PlaybackManager();

        File musicFolder = new File("C:\\Users\\sinoe\\Music\\JAVA");
        File playlistsDir = new File(System.getProperty("user.home"), "MyMusicPlayerPlaylist");
        playlistManager = new PlaylistManager(playlistsDir);

        loadSongsAndPlaylists(musicFolder);

        setupListeners();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                dispose();
                System.exit(0);
            }
        });

        setVisible(true);
    }

    private void loadSongsAndPlaylists(File musicFolder) {
        SwingWorker<Void, Song> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                musicManager.loadSongsFromFolder(musicFolder);
                System.out.println("CANCIONES TOTALES CARGADAS: " + musicManager.getAllSongs().size());
                return null;
            }

            @Override
            protected void done() {
                try {
                    loadPlaylists();
                    updateSongListUI();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(MainWindow.this, "ERROR CARGANDO PLAYLIST: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void updateSongListUI() {
        javax.swing.SwingUtilities.invokeLater(() -> {
            System.out.println("PLAYLIST ACTIVA: " + (activePlaylist != null ? activePlaylist.getName() : "null"));
            if (activePlaylist != null) {
                System.out.println("CANCIONES EN PLAY LIST ACTIVA: " + activePlaylist.getSongs().size());
                playerPanel.setSongs(activePlaylist.getSongs());
            }
        });
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

        System.out.println("PLAY LIST ACTIVA ESTABLECIDA: " + playlist.getName());
        System.out.println("CANCIONES CARGADAS EN LA PLAYLIST: " + playlist.getSongs().size());
        
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


        btnCreatePlaylist.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "NOMBRE DE NUEVA PLAYLIST:");
            if (name != null && !name.trim().isEmpty()) {
                Playlist newPlaylist = new Playlist(name.trim());
                try {
                    playlistManager.savePlaylist(newPlaylist);
                    playlistsCombo.addItem(newPlaylist.getName());
                    playlistsCombo.setSelectedItem(newPlaylist.getName());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "ERROR CREANDO PLAYLIST: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnDeletePlaylist.addActionListener(e -> {
            if (activePlaylist == null || activePlaylist.getName().equals("TODAS LAS CANCIONES")) {
                JOptionPane.showMessageDialog(this, "NO SE PUEDE ALIMINAR ESTA PLAYLIST.", "ATENCION", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int res = JOptionPane.showConfirmDialog(this, "¿ELIMINAR PLAYLIST " + activePlaylist.getName() + "?", "CONFIRMAR", JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.YES_OPTION) {
                playlistManager.deletePlaylist(activePlaylist);
                playlistsCombo.removeItem(activePlaylist.getName());
                if (playlistsCombo.getItemCount() > 0) {
                    playlistsCombo.setSelectedIndex(0);
                }
            }
        });
        
        playerPanel.setPlayAction(e -> {
            System.out.println("Play presionado");
            if (consumerPlayer != null) consumerPlayer.play();
        });
        
        playerPanel.setPlayAction(e -> {
    System.out.println("Play presionado");
    if (consumerPlayer != null) consumerPlayer.play();
});

playerPanel.setPauseAction(e -> {
    System.out.println("Pause presionado");
    if (consumerPlayer != null) consumerPlayer.pause();
});

playerPanel.setNextAction(e -> {
    System.out.println("Next presionado");
    if (consumerPlayer != null) {
        playbackManager.next();
        consumerPlayer.changeSong();
    }
});

playerPanel.setPrevAction(e -> {
    System.out.println("Prev presionado");
    if (consumerPlayer != null) {
        playbackManager.previous();
        consumerPlayer.changeSong();
    }
});

playerPanel.setPlaySongListener(index -> {
    System.out.println("Canción seleccionada con índice: " + index);
    if (consumerPlayer != null) {
        playbackManager.playSongAtIndex(index);
        consumerPlayer.changeSong();
    }
});

    }

    @Override
    public void dispose() {
        System.out.println("CERRANDO APLIACION...");
        if (consumerPlayer != null) {
            consumerPlayer.stopPlayback();
            consumerPlayer.interrupt();
            try {
                consumerPlayer.join(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        musicManager.shutdown();
        super.dispose();
    }
}