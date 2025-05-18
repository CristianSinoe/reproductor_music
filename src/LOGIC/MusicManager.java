/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LOGIC;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import DOMAIN.Song;
import DOMAIN.SongMetadata;
import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import java.io.File;


/**
 *
 * @author sinoe
 */
public class MusicManager {
    private final List<Song> allSongs = Collections.synchronizedList(new ArrayList<>());
    private final ExecutorService executor = Executors.newFixedThreadPool(8);

    public void loadSongsFromFolder(File folder) throws InterruptedException, ExecutionException {
        allSongs.clear();
        List<File> files = listAllMp3Files(folder);

        System.out.println("MP3 encontrados en carpeta: " + files.size());

        List<Future<Song>> futures = new ArrayList<>();
        for (File f : files) {
            Future<Song> future = executor.submit(() -> {
                SongMetadata metadata = extractMetadata(f);
                return new Song(f.getName(), f, metadata);
            });
            futures.add(future);
        }

        for (Future<Song> f : futures) {
            allSongs.add(f.get());
        }
    }

    private SongMetadata extractMetadata(File mp3File) {
        try {
            Mp3File mp3 = new Mp3File(mp3File);
            String artist = "Desconocido";
            String album = "Desconocido";
            int year = 0;

            if (mp3.hasId3v2Tag()) {
                ID3v2 tag = mp3.getId3v2Tag();
                artist = tag.getArtist() != null ? tag.getArtist() : artist;
                album = tag.getAlbum() != null ? tag.getAlbum() : album;
                try {
                    year = Integer.parseInt(tag.getYear());
                } catch (NumberFormatException e) {}
            } else if (mp3.hasId3v1Tag()) {
                ID3v1 tag = mp3.getId3v1Tag();
                artist = tag.getArtist() != null ? tag.getArtist() : artist;
                album = tag.getAlbum() != null ? tag.getAlbum() : album;
                try {
                    year = Integer.parseInt(tag.getYear());
                } catch (NumberFormatException e) {}
            }

            return new SongMetadata(artist, album, year);

        } catch (Exception e) {
            return new SongMetadata("Desconocido", "Desconocido", 0);
        }
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
