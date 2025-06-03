/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

import DOMAIN.Song;
import javax.swing.*;
import java.awt.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


/**
 *
 * @author sinoe
 */
public class PlayerPanel extends JPanel {
    private final JLabel lblNowPlaying = new JLabel("REPRODUCIENDO: NINGUNA");
    private final DefaultListModel<Song> songListModel = new DefaultListModel<>();
    private final JList<Song> songList = new JList<>(songListModel);
    private final JTabbedPane tabbedPane = new JTabbedPane();

    private final JLabel lblCoverSmall = new JLabel();
    private final JLabel lblTitle = new JLabel("Título canción");
    private final JLabel lblArtist = new JLabel("Intérprete");
    private final JSlider progressSlider = new JSlider();
    private final JButton btnPrevSmall = new JButton("⏮");
    private final JButton btnPlayPause = new JButton("▶️");
    private final JButton btnNextSmall = new JButton("⏭");

    private final JButton btnPlay = new JButton("▶️ PLAY");
    private final JButton btnPause = new JButton("⏸ PAUSA");
    private final JButton btnNext = new JButton("⏭ SIGUIENTE");
    private final JButton btnPrev = new JButton("⏮ ANTERIOR");
    private final JButton btnPrevPage = new JButton("⬅ Página anterior");
    private final JButton btnNextPage = new JButton("Página siguiente ➡");

    private List<Song> allSongs = new ArrayList<>();
    private int currentPage = 0;
    private static final int PAGE_SIZE = 100;

    private boolean isPlaying = false;

    public PlayerPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Texto arriba que muestra canción ahora
        lblNowPlaying.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblNowPlaying.setHorizontalAlignment(SwingConstants.CENTER);
        lblNowPlaying.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblNowPlaying, BorderLayout.NORTH);

        // Lista y paginación
        songList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        songList.setCellRenderer(new SongListRenderer());
        JScrollPane scrollPane = new JScrollPane(songList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("🎶 CANCIONES"));

        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        paginationPanel.add(btnPrevPage);
        paginationPanel.add(btnNextPage);

//        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER));
//        controls.add(btnPrev);
//        controls.add(btnPlay);
//        controls.add(btnPause);
//        controls.add(btnNext);

        JPanel songsPanel = new JPanel(new BorderLayout());
        songsPanel.add(paginationPanel, BorderLayout.NORTH);
        songsPanel.add(scrollPane, BorderLayout.CENTER);
//        songsPanel.add(controls, BorderLayout.SOUTH);

        tabbedPane.addTab("CANCIONES", songsPanel);
        tabbedPane.addTab("ALBUMES", new JLabel("PROXIMAMENTE..."));
        tabbedPane.addTab("ARTISTAS", new JLabel("PROXIMAMENTE..."));
        tabbedPane.addTab("AÑOS", new JLabel("PROXIMAMENTE..."));
        tabbedPane.addTab("PLAYLIST", new JLabel("PROXIMAMENTE..."));

        add(tabbedPane, BorderLayout.CENTER);

        // Panel inferior con carátula, título, artista, barra y botones pequeños
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        lblCoverSmall.setPreferredSize(new Dimension(80, 80));
        lblCoverSmall.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Cursor de mano para indicar clic
        bottomPanel.add(lblCoverSmall, BorderLayout.WEST);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblArtist.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoPanel.add(lblTitle);
        infoPanel.add(lblArtist);

        progressSlider.setMinimum(0);
        progressSlider.setMaximum(100);
        progressSlider.setValue(0);
        progressSlider.setPreferredSize(new Dimension(300, 20));
        infoPanel.add(progressSlider);

        JPanel smallControls = new JPanel(new FlowLayout(FlowLayout.CENTER));
        smallControls.add(btnPrevSmall);
        smallControls.add(btnPlayPause);
        smallControls.add(btnNextSmall);
        infoPanel.add(smallControls);

        bottomPanel.add(infoPanel, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);

        // --- LISTENER PARA REPRODUCIR CON ENTER ---
        songList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && playSongListener != null) {
                    int index = songList.getSelectedIndex();
                    if (index != -1) {
                        int actualIndex = currentPage * PAGE_SIZE + index;
                        playSongListener.playSongAt(actualIndex);
                    }
                }
            }
        });

        // Listener para abrir ventana grande al hacer click en carátula pequeña
        lblCoverSmall.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (openNowPlayingWindowListener != null) {
                    openNowPlayingWindowListener.openNowPlaying();
                }
            }
        });

        // Listeners botones pequeños
        btnPlayPause.addActionListener(e -> {
            if (isPlaying) {
                if (pauseAction != null) pauseAction.actionPerformed(e);
                btnPlayPause.setText("▶️");
                isPlaying = false;
            } else {
                if (playAction != null) playAction.actionPerformed(e);
                btnPlayPause.setText("⏸");
                isPlaying = true;
            }
        });

        btnPrevSmall.addActionListener(e -> {
            if (prevAction != null) prevAction.actionPerformed(e);
        });

        btnNextSmall.addActionListener(e -> {
            if (nextAction != null) nextAction.actionPerformed(e);
        });
        
        btnNextPage.addActionListener(e -> {
    int totalPages = (int) Math.ceil((double) allSongs.size() / PAGE_SIZE);
    if (currentPage < totalPages - 1) {
        currentPage++;
        loadPage(currentPage);
    }
});

btnPrevPage.addActionListener(e -> {
    if (currentPage > 0) {
        currentPage--;
        loadPage(currentPage);
    }
});

    }

    // Listeners acciones botones grandes
    private ActionListener playAction;
    private ActionListener pauseAction;
    private ActionListener nextAction;
    private ActionListener prevAction;

    public void setPlayAction(ActionListener listener) {
        this.playAction = listener;
        btnPlay.addActionListener(listener);
    }

    public void setPauseAction(ActionListener listener) {
        this.pauseAction = listener;
        btnPause.addActionListener(listener);
    }

    public void setNextAction(ActionListener listener) {
        this.nextAction = listener;
        btnNext.addActionListener(listener);
    }

    public void setPrevAction(ActionListener listener) {
        this.prevAction = listener;
        btnPrev.addActionListener(listener);
    }

    public void setNowPlaying(String songName) {
        lblNowPlaying.setText("REPRODUCIENDO: " + songName);

        Song song = allSongs.stream()
                .filter(s -> s.getName().equals(songName))
                .findFirst()
                .orElse(null);

        if (song != null && song.getMetadata() != null) {
            lblTitle.setText(song.getName());
            lblArtist.setText(song.getMetadata().getArtist() != null ? song.getMetadata().getArtist() : "Desconocido");

            String coverPath = song.getMetadata().getCoverImagePath();
            if (coverPath != null && !coverPath.isBlank()) {
                new Thread(() -> {
                    try {
                        File file = new File(coverPath);
                        if (file.exists()) {
                            ImageIcon icon = new ImageIcon(file.toURI().toURL());
                            Image img = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                            ImageIcon scaledIcon = new ImageIcon(img);
                            SwingUtilities.invokeLater(() -> lblCoverSmall.setIcon(scaledIcon));
                        } else {
                            SwingUtilities.invokeLater(() -> lblCoverSmall.setIcon(null));
                        }
                    } catch (Exception e) {
                        System.err.println("Error cargando carátula: " + e.getMessage());
                    }
                }).start();
            } else {
                lblCoverSmall.setIcon(null);
            }
        } else {
            lblTitle.setText("Título desconocido");
            lblArtist.setText("Intérprete desconocido");
            lblCoverSmall.setIcon(null);
        }
    }

    public void addSongs(List<Song> songs) {
        if (allSongs == null) {
            allSongs = new ArrayList<>();
        }
        allSongs.addAll(songs);
        if (allSongs.size() <= PAGE_SIZE || songListModel.isEmpty()) {
            loadPage(currentPage);
        }
    }

    public void loadPage(int pageIndex) {
        songListModel.clear();
        if (allSongs == null) return;

        int start = pageIndex * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, allSongs.size());

        for (int i = start; i < end; i++) {
            songListModel.addElement(allSongs.get(i));
        }

        songList.revalidate();
        songList.repaint();
    }

    public List<Song> getSongs() {
        List<Song> songs = new ArrayList<>();
        for (int i = 0; i < songListModel.size(); i++) {
            songs.add(songListModel.getElementAt(i));
        }
        return songs;
    }

    public void setSelectedSongIndex(int index) {
        int pageOfIndex = index / PAGE_SIZE;
        if (pageOfIndex != currentPage) {
            currentPage = pageOfIndex;
            loadPage(currentPage);
        }
        int indexInPage = index % PAGE_SIZE;
        songList.setSelectedIndex(indexInPage);
        songList.ensureIndexIsVisible(indexInPage);
    }

    public interface PlaySongListener {
        void playSongAt(int index);
    }

    private PlaySongListener playSongListener;

    public void setPlaySongListener(PlaySongListener listener) {
        this.playSongListener = listener;
    }

    public JList<Song> getSongList() {
        return songList;
    }

    public void setSongs(List<Song> songs) {
        allSongs = new ArrayList<>();
        currentPage = 0;
        songListModel.clear();

        SwingWorker<Void, List<Song>> loader = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                int blockSize = 10;

                for (int i = 0; i < songs.size(); i += blockSize) {
                    int end = Math.min(i + blockSize, songs.size());
                    List<Song> block = songs.subList(i, end);
                    allSongs.addAll(block);
                    publish(new ArrayList<>(block)); // enviar bloque para mostrar
                    Thread.sleep(30);
                }
                return null;
            }

            @Override
            protected void process(List<List<Song>> chunks) {
                for (List<Song> block : chunks) {
                    for (Song s : block) {
                        songListModel.addElement(s);
                    }
                }
                songList.revalidate();
                songList.repaint();
            }

            @Override
            protected void done() {
                tabbedPane.setSelectedIndex(0);
            }
        };

        loader.execute();
    }

    public JSlider getProgressSlider() {
        return progressSlider;
    }
    
    public void setProgress(int value) {
        progressSlider.setValue(value);
    }

    // Nueva interfaz para abrir ventana NowPlayingWindow
    public interface OpenNowPlayingWindowListener {
        void openNowPlaying();
    }

    private OpenNowPlayingWindowListener openNowPlayingWindowListener;

    public void setOpenNowPlayingWindowListener(OpenNowPlayingWindowListener listener) {
        this.openNowPlayingWindowListener = listener;
    }
    
    public void setCoverClickListener(MouseListener listener) {
    lblCoverSmall.addMouseListener(listener);
    lblCoverSmall.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
}

}
