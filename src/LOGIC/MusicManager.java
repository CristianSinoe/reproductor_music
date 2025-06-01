/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LOGIC;

import DOMAIN.MetadataExtractor;
import java.util.*;
import java.util.concurrent.*;
import DOMAIN.Song;
import DOMAIN.SongMetadata;
import java.io.File;


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

                // 🟩 Aquí agregas los println
                System.out.println("Cargando canción: " + song.getName());
                System.out.println("Cover path asignado: " + song.getMetadata().getCoverImagePath());

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

    public List<Song> getAllSongs() {
        return allSongs;
    }

    public void shutdown() {
        executor.shutdown();
    }
}
