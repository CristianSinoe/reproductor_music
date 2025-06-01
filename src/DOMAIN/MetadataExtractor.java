/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DOMAIN;

import com.mpatric.mp3agic.*;

import java.io.File;
import java.io.FileOutputStream;

/**
 *
 * @author sinoe
 */
public class MetadataExtractor {

    public static SongMetadata extractMetadata(File mp3File) {
        try {
            Mp3File mp3 = new Mp3File(mp3File);

            String artist = "Desconocido";
            String album = "Desconocido";
            int year = 0;
            String coverPath = null;

            if (mp3.hasId3v2Tag()) {
                System.out.println(mp3File.getName() + " tiene etiqueta ID3v2");
                ID3v2 id3v2Tag = mp3.getId3v2Tag();

                artist = id3v2Tag.getArtist() != null ? id3v2Tag.getArtist() : artist;
                album = id3v2Tag.getAlbum() != null ? id3v2Tag.getAlbum() : album;

                try {
                    year = Integer.parseInt(id3v2Tag.getYear());
                } catch (NumberFormatException e) {
                }

                byte[] imageData = id3v2Tag.getAlbumImage();
                if (imageData != null) {
                    System.out.println(mp3File.getName() + " tiene imagen embebida, tamaño: " + imageData.length);

                    String coverDirPath = System.getProperty("user.home") + "/MyMusicPlayer/covers/";
                    File coverDir = new File(coverDirPath);
                    if (!coverDir.exists()) {
                        boolean created = coverDir.mkdirs();
                        System.out.println("Carpeta para carátulas creada: " + created);
                    }

                    String safeName = mp3File.getName().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
                    File imgFile = new File(coverDir, "cover_" + safeName + ".jpg");

                    try (FileOutputStream fos = new FileOutputStream(imgFile)) {
                        fos.write(imageData);
                    }

                    coverPath = imgFile.getAbsolutePath();
                    System.out.println("Carátula guardada en: " + coverPath);
                } else {
                    System.out.println(mp3File.getName() + " NO tiene imagen embebida");
                }
            } else {
                System.out.println(mp3File.getName() + " NO tiene etiqueta ID3v2");
            }

            if (coverPath == null) {
                File dir = mp3File.getParentFile();
                File coverFile = new File(dir, "cover.jpg");
                if (!coverFile.exists()) {
                    coverFile = new File(dir, "folder.jpg");
                }
                if (coverFile.exists()) {
                    coverPath = coverFile.getAbsolutePath();
                    System.out.println("Carátula externa encontrada en: " + coverPath);
                } else {
                    System.out.println("No se encontró carátula externa para " + mp3File.getName());
                }
            }

            return new SongMetadata(artist, album, year, coverPath);

        } catch (Exception e) {
            e.printStackTrace();
            return new SongMetadata("Desconocido", "Desconocido", 0, null);
        }
    }
}
