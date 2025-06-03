/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

import javax.swing.*;
import java.awt.*;
import DOMAIN.Song;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author sinoe
 */
public class SongListRenderer extends JPanel implements ListCellRenderer<Song> {

    private static final ImageIcon DEFAULT_ICON = new ImageIcon(
        SongListRenderer.class.getResource("/resources/images/default_cover02.png")
    );

    private static final int IMAGE_SIZE = 200;

    private static final Map<String, ImageIcon> coverCache = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, ImageIcon> eldest) {
            return size() > 200;
        }
    };

    private static final ExecutorService imageLoaderPool = Executors.newFixedThreadPool(5);

    private JLabel lblCover = new JLabel();
    private JLabel lblTitle = new JLabel();
    private JLabel lblArtistAlbum = new JLabel();

    public SongListRenderer() {
        setLayout(new BorderLayout(10, 10));
        lblCover.setPreferredSize(new Dimension(IMAGE_SIZE, IMAGE_SIZE));

        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblArtistAlbum.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.add(lblTitle);
        textPanel.add(lblArtistAlbum);

        add(lblCover, BorderLayout.WEST);
        add(textPanel, BorderLayout.CENTER);

        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
    
    @Override
    public Component getListCellRendererComponent(
            JList<? extends Song> list,
            Song song,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {

        lblTitle.setText(song.getMetadata().getArtist() + " - " + song.getName());
        lblArtistAlbum.setText(song.getMetadata().getAlbum());
        lblCover.setIcon(DEFAULT_ICON);

        String coverPath = song.getMetadata().getCoverImagePath();

        if (coverPath != null && !coverPath.isBlank()) {
            if (coverCache.containsKey(coverPath)) {
                lblCover.setIcon(coverCache.get(coverPath));
            } else {
                imageLoaderPool.submit(() -> {
                    try {
                        File file = new File(coverPath);
                        if (file.exists()) {
                            ImageIcon icon = new ImageIcon(file.toURI().toURL());
                            Image img = icon.getImage().getScaledInstance(IMAGE_SIZE, IMAGE_SIZE, Image.SCALE_SMOOTH);
                            ImageIcon scaledIcon = new ImageIcon(img);

                            SwingUtilities.invokeLater(() -> {
                                coverCache.put(coverPath, scaledIcon);
                                lblCover.setIcon(scaledIcon);

                                Rectangle cellBounds = list.getCellBounds(index, index);
                                if (cellBounds != null) {
                                    list.repaint(cellBounds);
                                }
                            });
                        }
                    } catch (Exception e) {
                        System.err.println("ERROR CARGANDO IMAGEN EN HILO: " + e.getMessage());
                    }
                });
            }
        }

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            lblTitle.setForeground(list.getSelectionForeground());
            lblArtistAlbum.setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            lblTitle.setForeground(list.getForeground());
            lblArtistAlbum.setForeground(list.getForeground());
        }

        setOpaque(true);
        return this;
    }
}
