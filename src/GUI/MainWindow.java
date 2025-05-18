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
import DOMAIN.SongMetadata;
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
    private JButton btnCreatePlaylist = new JButton("Nueva Playlist");
    private JButton btnDeletePlaylist = new JButton("Eliminar Playlist");
    private JButton btnRenamePlaylist = new JButton("Renombrar Playlist");
    private JButton btnAddSongToPlaylist = new JButton("Añadir Canción a Playlist");

    private Playlist activePlaylist;

    public MainWindow() {
        setTitle("🎵 Reproductor de Música");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Playlists:"));
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

        File musicFolder = new File("C:\\Users\\sinoe\\Music");
        File playlistsDir = new File(System.getProperty("user.home"), "MyMusicPlayerPlaylists");
        playlistManager = new PlaylistManager(playlistsDir);

        loadSongsAndPlaylists(musicFolder);

        setupListeners();

        // Manejo del cierre
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                dispose();
                System.exit(0); // fuerza cierre
            }
        });

        setVisible(true);
    }

    private void loadSongsAndPlaylists(File musicFolder) {
        SwingWorker<Void, Song> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                musicManager.loadSongsFromFolder(musicFolder);
                System.out.println("Canciones totales cargadas: " + musicManager.getAllSongs().size());
                return null;
            }

            @Override
            protected void done() {
                try {
                    loadPlaylists();
                    updateSongListUI();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(MainWindow.this, "Error cargando playlists: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void updateSongListUI() {
        javax.swing.SwingUtilities.invokeLater(() -> {
            System.out.println("Playlist activa: " + (activePlaylist != null ? activePlaylist.getName() : "null"));
            if (activePlaylist != null) {
                System.out.println("Canciones en playlist activa: " + activePlaylist.getSongs().size());
                playerPanel.setSongs(activePlaylist.getSongs().stream().map(Song::getName).collect(Collectors.toList()));
            }
        });
    }

    private void loadPlaylists() throws IOException {
        List<Playlist> playlists = playlistManager.loadAll().stream().filter(p -> p != null).collect(Collectors.toList());

        if (playlists.isEmpty()) {
            Playlist allSongs = new Playlist("Todas las canciones");
            allSongs.getSongs().addAll(musicManager.getAllSongs());
            playlistManager.savePlaylist(allSongs);
            playlists.add(allSongs);
        }

        playlistsCombo.removeAllItems();
        for (Playlist p : playlists) {
            playlistsCombo.addItem(p.getName());
        }

        ignoreComboEvents = true;
        playlistsCombo.setSelectedItem(playlists.get(0).getName()); // esto dispara el evento
        ignoreComboEvents = false;

    }

    private void setActivePlaylist(Playlist playlist) {
        activePlaylist = playlist;
        playbackManager.setPlaylist(activePlaylist);

        System.out.println("Playlist activa establecida: " + playlist.getName());
        System.out.println("Canciones cargadas en la playlist: " + playlist.getSongs().size());

        playerPanel.setSongs(playlist.getSongs().stream().map(Song::getName).collect(Collectors.toList()));

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
    if (ignoreComboEvents) return; // 🔥 evita llamada doble

    String selected = (String) playlistsCombo.getSelectedItem();
    if (selected != null) {
        try {
            List<Playlist> playlists = playlistManager.loadAll();
            playlists.stream()
                .filter(p -> p.getName().equals(selected))
                .findFirst()
                .ifPresent(this::setActivePlaylist);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error cargando playlist: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
});


        btnCreatePlaylist.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Nombre nueva playlist:");
            if (name != null && !name.trim().isEmpty()) {
                Playlist newPlaylist = new Playlist(name.trim());
                try {
                    playlistManager.savePlaylist(newPlaylist);
                    playlistsCombo.addItem(newPlaylist.getName());
                    playlistsCombo.setSelectedItem(newPlaylist.getName());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error creando playlist: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnDeletePlaylist.addActionListener(e -> {
            if (activePlaylist == null || activePlaylist.getName().equals("Todas las canciones")) {
                JOptionPane.showMessageDialog(this, "No se puede eliminar esta playlist.", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int res = JOptionPane.showConfirmDialog(this, "¿Eliminar playlist " + activePlaylist.getName() + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.YES_OPTION) {
                playlistManager.deletePlaylist(activePlaylist);
                playlistsCombo.removeItem(activePlaylist.getName());
                if (playlistsCombo.getItemCount() > 0) {
                    playlistsCombo.setSelectedIndex(0);
                }
            }
        });

        playerPanel.setPlayAction(e -> {
            if (consumerPlayer != null) consumerPlayer.play();
        });

        playerPanel.setPauseAction(e -> {
            if (consumerPlayer != null) consumerPlayer.pause();
        });

        playerPanel.setNextAction(e -> {
            if (consumerPlayer != null) {
                playbackManager.getNextSong();
                consumerPlayer.interrupt();
            }
        });

        playerPanel.setPrevAction(e -> {
            if (consumerPlayer != null) {
                playbackManager.getPreviousSong();
                consumerPlayer.interrupt();
            }
        });

        playerPanel.setPlaySongListener(index -> {
            if (consumerPlayer != null) {
                playbackManager.playSongAtIndex(index);
                consumerPlayer.interrupt();
            }
        });
    }

    @Override
    public void dispose() {
        System.out.println("Cerrando aplicación...");
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