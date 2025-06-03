/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LOGIC;

import DOMAIN.MetadataExtractor;
import DOMAIN.Song;
import DOMAIN.SongMetadata;
import com.mpatric.mp3agic.Mp3File;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



/**
 *
 * @author sinoe
 */
public class MusicManager {
    private final List<Song> allSongs = Collections.synchronizedList(new ArrayList<>());
    private final ExecutorService executor = Executors.newFixedThreadPool(8);

    public List<Song> loadSongsFromFolder(File folder) {
        List<Song> songs = new ArrayList<>();
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".mp3")) {
                    SongMetadata metadata = MetadataExtractor.extractMetadata(file);
                    Song song = new Song(file.getName(), file, metadata);

                    // Obtener duración con MP3agic
                    try {
                        Mp3File mp3 = new Mp3File(file);
                        int durationMs = (int) (mp3.getLengthInSeconds() * 1000);
                        song.setDurationMillis(durationMs);
                    } catch (Exception e) {
                        System.err.println("No se pudo obtener duración para: " + file.getName());
                        song.setDurationMillis(0);
                    }

                    System.out.println("Cargando canción: " + song.getName());
                    System.out.println("Cover path asignado: " + song.getMetadata().getCoverImagePath());
                    System.out.println("Duración (ms): " + song.getDurationMillis());

                    songs.add(song);
                }
            }
        }

        return songs;
    }

    private List<File> listAllMp3Files(File folder) {
        List<File> files = new ArrayList<>();
        File[] items = folder.listFiles();
        if (items != null) {
            for (File f : items) {
                if (f.isDirectory()) {
                    files.addAll(listAllMp3Files(f));
                } else if (f.getName().toLowerCase().endsWith(".mp3")) {
                    files.add(f);
                }
            }
        }
        return files;
    }

    public Song loadSong(File file) {
        try {
            if (file.getName().toLowerCase().endsWith(".mp3")) {
                SongMetadata metadata = MetadataExtractor.extractMetadata(file);
                Song song = new Song(metadata.getArtist(), metadata.getAlbum(), metadata.getYear(), metadata.getCoverImagePath(), file.getAbsolutePath());
                // Obtener duración
                try {
                    Mp3File mp3 = new Mp3File(file);
                    int durationMs = (int) (mp3.getLengthInSeconds() * 1000);
                    song.setDurationMillis(durationMs);
                } catch (Exception e) {
                    System.err.println("No se pudo obtener duración para: " + file.getName());
                    song.setDurationMillis(0);
                }
                allSongs.add(song);
                return song;
            }
        } catch (Exception e) {
            System.err.println("Error cargando metadata para: " + file.getName());
            e.printStackTrace();
        }
        return null;
    }

    public List<Song> getAllSongs() {
        return allSongs;
    }

    public void shutdown() {
        executor.shutdown();
    }
}
