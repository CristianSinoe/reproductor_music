/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DOMAIN;

import com.mpatric.mp3agic.*;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.imageio.ImageIO;
import java.io.IOException;


/**
 *
 * @author sinoe
 */
public class MetadataExtractor {
    
    private static final int MAX_THREADS = 6;
    private static final ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);

    public static void extractMetadataAsync(File mp3File, Consumer<SongMetadata> callback) {
        executor.submit(() -> {
            SongMetadata metadata = extractMetadata(mp3File);
            if (callback != null) {
                callback.accept(metadata);
            }
        });
    }

    public static SongMetadata extractMetadata(File mp3File) {
        SongMetadata meta = new SongMetadata();
        try {
            Mp3File mp3 = new Mp3File(mp3File);

            String artist = "DESCONOCIDO";
            String album = "DESCONOCIDO";
            int year = 0;
            String coverPath = null;

            if (mp3.hasId3v2Tag()) {
                ID3v2 id3v2Tag = mp3.getId3v2Tag();

                artist = id3v2Tag.getArtist() != null ? id3v2Tag.getArtist() : artist;
                album = id3v2Tag.getAlbum() != null ? id3v2Tag.getAlbum() : album;
                year = id3v2Tag.getYear() != null ? Integer.parseInt(id3v2Tag.getYear()) : year;

                byte[] albumImageData = id3v2Tag.getAlbumImage();
                if (albumImageData != null) {
                    String coverFileName = "cover_" + mp3File.getName().replace(".mp3", ".jpg");
                    File coverFile = new File("covers", coverFileName);
                    coverFile.getParentFile().mkdirs();

                    try (InputStream in = new ByteArrayInputStream(albumImageData)) {
                        BufferedImage originalImage = ImageIO.read(in);
                        if (originalImage != null) {
                            BufferedImage resizedImage = resize(originalImage, 200, 200);
                            ImageIO.write(resizedImage, "jpg", coverFile);
                            coverPath = coverFile.getAbsolutePath();
                        }
                    } catch (IOException ex) {
                        System.err.println("ERRO REDIMENSIONANDO IMAGEN: " + mp3File.getName() + " -> " + ex.getMessage());
                    }
                }
            }

            meta.setArtist(artist);
            meta.setAlbum(album);
            meta.setYear(year);
            meta.setCoverImagePath(coverPath);

        } catch (Exception e) {
            System.err.println("ERROR LEYENDO METADATOS DE: " + mp3File.getName() + " -> " + e.getMessage());
        }

        return meta;
    }

    private static BufferedImage resize(BufferedImage img, int width, int height) {
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resized.createGraphics();
        g.drawImage(img, 0, 0, width, height, null);
        g.dispose();
        return resized;
    }

    public static void shutdown() {
        executor.shutdown();
    }
}
