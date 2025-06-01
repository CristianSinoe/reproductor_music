/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

import DOMAIN.Song;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

/**
 *
 * @author sinoe
 */
public class PlayerPanel extends JPanel {
    private final JLabel lblNowPlaying = new JLabel("REPRODUCIENDO: NINGUNA");
    private final DefaultListModel<Song> songListModel = new DefaultListModel<>();
    private final JList<Song> songList = new JList<>(songListModel);

    private final JButton btnPlay = new JButton("▶️ PLAY");
    private final JButton btnPause = new JButton("⏸ PAUSA");
    private final JButton btnNext = new JButton("⏭ SIGUIENTE");
    private final JButton btnPrev = new JButton("⏮ ANTERIOR");
    
    private final JLabel lblCoverLarge = new JLabel();
    private final JTabbedPane tabbedPane = new JTabbedPane();

    public PlayerPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        lblNowPlaying.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblNowPlaying.setHorizontalAlignment(SwingConstants.CENTER);
        lblNowPlaying.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        songList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        songList.setCellRenderer(new SongListRenderer()); // Aquí asignas el renderer personalizado

        JScrollPane scrollPane = new JScrollPane(songList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("🎶 CANCIONES"));

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER));
        controls.add(btnPrev);
        controls.add(btnPlay);
        controls.add(btnPause);
        controls.add(btnNext);

        JPanel songsPanel = new JPanel(new BorderLayout());
        songsPanel.add(scrollPane, BorderLayout.CENTER);
        songsPanel.add(controls, BorderLayout.SOUTH);

        tabbedPane.addTab("CANCIONES", songsPanel);
        tabbedPane.addTab("ALBUMES", new JLabel("PROXIMAMENTE..."));
        tabbedPane.addTab("ARTISTAS", new JLabel("PROXIMAMENTE..."));
        tabbedPane.addTab("AÑOS", new JLabel("PROXIMAMENTE..."));
        tabbedPane.addTab("PLAYLIST", new JLabel("PROXIMAMENTE..."));
        
        lblCoverLarge.setHorizontalAlignment(SwingConstants.CENTER);
lblCoverLarge.setPreferredSize(new Dimension(200, 200));
lblCoverLarge.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
add(lblCoverLarge, BorderLayout.WEST);

        add(lblNowPlaying, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        songList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && playSongListener != null) {
                    int index = songList.getSelectedIndex();
                    if (index != -1) {
                        playSongListener.playSongAt(index);
                    }
                }
            }
        });
    }

    // Cambiado para recibir lista completa de objetos Song
    public void setSongs(List<Song> songs) {
    songListModel.clear();

    for (Song s : songs) {
        songListModel.addElement(s);
    }
    songList.revalidate();
    tabbedPane.setSelectedIndex(0); // Esto es correcto
}


    public JList<Song> getSongList() {
        return songList;
    }

    public void setNowPlaying(String songName) {
    lblNowPlaying.setText("REPRODUCIENDO: " + songName);

    Song song = null;
    for (int i = 0; i < songListModel.size(); i++) {
        Song s = songListModel.get(i);
        if (s.getName().equals(songName)) {
            song = s;
            break;
        }
    }

    if (song != null && song.getMetadata() != null) {
        String coverPath = song.getMetadata().getCoverImagePath();
        if (coverPath != null && !coverPath.isBlank()) {
            new Thread(() -> {
                try {
                    File file = new File(coverPath);
                    if (file.exists()) {
                        ImageIcon icon = new ImageIcon(file.toURI().toURL());
                        Image img = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                        ImageIcon scaledIcon = new ImageIcon(img);
                        SwingUtilities.invokeLater(() -> lblCoverLarge.setIcon(scaledIcon));
                    }
                } catch (Exception e) {
                    System.err.println("Error cargando carátula actual: " + e.getMessage());
                }
            }).start();
        } else {
            lblCoverLarge.setIcon(null); // o un ícono por defecto
        }
    } else {
        lblCoverLarge.setIcon(null);
    }
}


    public interface PlaySongListener {
        void playSongAt(int index);
    }

    private PlaySongListener playSongListener;

    public void setPlaySongListener(PlaySongListener listener) {
        this.playSongListener = listener;
    }

    public void setPlayAction(java.awt.event.ActionListener listener) {
        btnPlay.addActionListener(listener);
    }

    public void setPauseAction(java.awt.event.ActionListener listener) {
        btnPause.addActionListener(listener);
    }

    public void setNextAction(java.awt.event.ActionListener listener) {
        btnNext.addActionListener(listener);
    }

    public void setPrevAction(java.awt.event.ActionListener listener) {
        btnPrev.addActionListener(listener);
    }
}