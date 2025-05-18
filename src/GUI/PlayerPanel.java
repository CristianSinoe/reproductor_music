/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;


/**
 *
 * @author sinoe
 */
public class PlayerPanel extends JPanel {
    private final JLabel lblNowPlaying = new JLabel("Reproduciendo: Ninguna");
    private final DefaultListModel<String> songListModel = new DefaultListModel<>();
    private final JList<String> songList = new JList<>(songListModel);

    private final JButton btnPlay = new JButton("▶️ Play");
    private final JButton btnPause = new JButton("⏸ Pause");
    private final JButton btnNext = new JButton("⏭ Next");
    private final JButton btnPrev = new JButton("⏮ Prev");

    private final JTabbedPane tabbedPane = new JTabbedPane();

    public PlayerPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        lblNowPlaying.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblNowPlaying.setHorizontalAlignment(SwingConstants.CENTER);
        lblNowPlaying.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        songList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(songList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("🎶 Canciones"));

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER));
        controls.add(btnPrev);
        controls.add(btnPlay);
        controls.add(btnPause);
        controls.add(btnNext);

        JPanel songsPanel = new JPanel(new BorderLayout());
        songsPanel.add(scrollPane, BorderLayout.CENTER);
        songsPanel.add(controls, BorderLayout.SOUTH);

        tabbedPane.addTab("Canciones", songsPanel);
        tabbedPane.addTab("Álbumes", new JLabel("Próximamente..."));
        tabbedPane.addTab("Artistas", new JLabel("Próximamente..."));
        tabbedPane.addTab("Años", new JLabel("Próximamente..."));
        tabbedPane.addTab("Playlists", new JLabel("Próximamente..."));

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

    public void setNowPlaying(String songName) {
        lblNowPlaying.setText("🎵 Reproduciendo: " + songName);
    }

    public void setSongs(List<String> songs) {
        songListModel.clear();
        songs.forEach(songListModel::addElement);

        songList.revalidate(); // 🔧 Forzar refresco visual
        songList.repaint();

        tabbedPane.setSelectedIndex(0); // 🔧 Mostrar pestaña de Canciones
    }

    public JList<String> getSongList() {
        return songList;
    }

    public interface PlaySongListener {
        void playSongAt(int index);
    }

    private PlaySongListener playSongListener;

    public void setPlaySongListener(PlaySongListener listener) {
        this.playSongListener = listener;
    }

    public void setPlayAction(ActionListener listener) {
        btnPlay.addActionListener(listener);
    }

    public void setPauseAction(ActionListener listener) {
        btnPause.addActionListener(listener);
    }

    public void setNextAction(ActionListener listener) {
        btnNext.addActionListener(listener);
    }

    public void setPrevAction(ActionListener listener) {
        btnPrev.addActionListener(listener);
    }
}