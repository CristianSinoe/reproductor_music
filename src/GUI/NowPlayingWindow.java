/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

import javax.swing.*;
import java.awt.*;
import DOMAIN.Song;
import LOGIC.ConsumerPlayer;
import LOGIC.PlaybackManager;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;


/**
 *
 * @author sinoe
 */
public class NowPlayingWindow extends JDialog {

    private final JLabel lblCoverLarge = new JLabel();
    private final JLabel lblTitle = new JLabel("Título canción");
    private final JLabel lblArtist = new JLabel("Intérprete");
    private final JSlider progressSlider = new JSlider();

    private final JButton btnPrev = new JButton();
    private final JButton btnPlayPause = new JButton();
    private final JButton btnNext = new JButton();
    private final JButton btnClose = new JButton("X");

    private boolean isPlaying = false;

    private ConsumerPlayer consumerPlayer;
    private Song currentSong;

    private Timer progressTimer;

    public NowPlayingWindow(JFrame owner, Song song, ConsumerPlayer consumerPlayer) {
        super(owner, "Reproduciendo ahora", true);
        this.consumerPlayer = consumerPlayer;
        this.currentSong = song;

        setUndecorated(true);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(0, 0, screenSize.width, screenSize.height);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(60, 60));
        getContentPane().setBackground(Color.WHITE);

        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        closePanel.setOpaque(false);
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 24));
        btnClose.setForeground(Color.DARK_GRAY);
        btnClose.setBorderPainted(false);
        btnClose.setContentAreaFilled(false);
        btnClose.setFocusPainted(false);
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> {
            if (progressTimer != null) progressTimer.stop();
            dispose();
        });
        closePanel.add(btnClose);
        add(closePanel, BorderLayout.NORTH);

        lblCoverLarge.setPreferredSize(new Dimension(500, 500));
        lblCoverLarge.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel coverWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 40, 0));
        coverWrapper.setOpaque(false);
        coverWrapper.add(lblCoverLarge);
        add(coverWrapper, BorderLayout.WEST);

        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 48));
        lblTitle.setForeground(Color.BLACK);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblArtist.setFont(new Font("Segoe UI", Font.PLAIN, 30));
        lblArtist.setForeground(Color.DARK_GRAY);
        lblArtist.setAlignmentX(Component.CENTER_ALIGNMENT);

        progressSlider.setMinimum(0);
        progressSlider.setMaximum(100);
        progressSlider.setValue(0);
        progressSlider.setPreferredSize(new Dimension(screenSize.width / 4, 30));
        progressSlider.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 80, 20));
        buttonsPanel.setOpaque(false);

        btnPrev.setIcon(loadAndScaleIcon("/resources/images/prev.png", 64, 64));
        btnPlayPause.setIcon(loadAndScaleIcon("/resources/images/pause.png", 64, 64));
        btnNext.setIcon(loadAndScaleIcon("/resources/images/next.png", 64, 64));
        btnClose.setText("X");

        buttonsPanel.add(btnPrev);
        buttonsPanel.add(btnPlayPause);
        buttonsPanel.add(btnNext);

        infoPanel.add(Box.createVerticalGlue());
        infoPanel.add(lblTitle);
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(lblArtist);
        infoPanel.add(Box.createVerticalStrut(40));
        infoPanel.add(progressSlider);
        infoPanel.add(Box.createVerticalStrut(40));
        infoPanel.add(buttonsPanel);
        infoPanel.add(Box.createVerticalGlue());

        add(infoPanel, BorderLayout.CENTER);

        updateSongInfo(song);
        isPlaying = true;
        
        progressTimer = new Timer(500, e -> updateProgress());
        progressTimer.start();

        btnPlayPause.addActionListener(e -> togglePlayPause());
        btnPrev.addActionListener(e -> {
            if (consumerPlayer != null) playbackPrevious();
        });
        btnNext.addActionListener(e -> {
            if (consumerPlayer != null) playbackNext();
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (progressTimer != null) progressTimer.stop();
                dispose();
            }
        });
    }

    private ImageIcon loadAndScaleIcon(String path, int width, int height) {
        try {
            URL url = getClass().getResource(path);
            if (url == null) return null;
            ImageIcon icon = new ImageIcon(url);
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            System.err.println("ERRO CARGANDO ICONO DE " + path + ": " + e.getMessage());
            return null;
        }
    }

    public void updateSongInfo(Song song) {
        if (song == null) return;
        this.currentSong = song;
        lblTitle.setText(song.getName());
        String artist = (song.getMetadata() != null && song.getMetadata().getArtist() != null)
                ? song.getMetadata().getArtist()
                : "Desconocido";
        lblArtist.setText(artist);

        new Thread(() -> {
            try {
                if (song.getMetadata() != null) {
                    String coverPath = song.getMetadata().getCoverImagePath();
                    if (coverPath != null && !coverPath.isBlank()) {
                        File file = new File(coverPath);
                        if (file.exists()) {
                            ImageIcon icon = new ImageIcon(file.toURI().toURL());
                            Image img = icon.getImage().getScaledInstance(500, 500, Image.SCALE_SMOOTH);
                            ImageIcon scaledIcon = new ImageIcon(img);
                            SwingUtilities.invokeLater(() -> {
                                lblCoverLarge.setIcon(scaledIcon);
                                lblCoverLarge.repaint();
                                lblCoverLarge.revalidate();
                            });
                            return;
                        }
                    }
                }
                SwingUtilities.invokeLater(() -> lblCoverLarge.setIcon(null));
            } catch (Exception ex) {
                System.err.println("ERROR CRGANDO CARATULA GRANDE: " + ex.getMessage());
                SwingUtilities.invokeLater(() -> lblCoverLarge.setIcon(null));
            }
        }).start();

        progressSlider.setValue(0);
    }

    private void updateProgress() {
        if (consumerPlayer == null || currentSong == null) return;
        int progress = consumerPlayer.getProgressPercent();
        progressSlider.setValue(progress);
    }

    private void playbackPrevious() {
        try {
            PlaybackManager pm = getPlaybackManager();
            if (pm != null) {
                pm.previous();
                consumerPlayer.changeSong();
                updateSongInfo(pm.getCurrentSong());
            }
        } catch (Exception ex) {
            updateSongInfo(currentSong);
        }
    }

    private void playbackNext() {
        try {
            PlaybackManager pm = getPlaybackManager();
            if (pm != null) {
                pm.next();
                consumerPlayer.changeSong();
                updateSongInfo(pm.getCurrentSong());
            }
        } catch (Exception ex) {
            updateSongInfo(currentSong);
        }
    }

    private PlaybackManager getPlaybackManager() {
        try {
            java.lang.reflect.Field field = consumerPlayer.getClass().getDeclaredField("playbackManager");
            field.setAccessible(true);
            return (PlaybackManager) field.get(consumerPlayer);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void togglePlayPause() {
    if (consumerPlayer == null) return;
    if (isPlaying) {
        consumerPlayer.pause();
        btnPlayPause.setIcon(loadAndScaleIcon("/resources/images/play.png", 64, 64));
        isPlaying = false;
    } else {
        consumerPlayer.play();
        btnPlayPause.setIcon(loadAndScaleIcon("/resources/images/pause.png", 64, 64));
        isPlaying = true;
    }
}
}
