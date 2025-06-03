/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LOGIC;

import DOMAIN.Song;
import java.io.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sinoe
 */
public class Playlist {
    
    private String name;
    private List<Song> songs = new ArrayList<>();

    public Playlist() {}

    public Playlist(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public void addSong(Song song) {
        songs.add(song);
    }

    public void removeSong(Song song) {
        songs.remove(song);
    }

    public void saveToFile(File dir) throws IOException {
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, sanitizeFileName(name) + ".json");

        Gson gson = new GsonBuilder()
                .excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT)
                .setPrettyPrinting()
                .create();

        try (Writer writer = new FileWriter(file)) {
            gson.toJson(this, writer);
        }
    }

    public static Playlist loadFromFile(File file) throws IOException {
    Gson gson = new GsonBuilder()
            .excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT)
            .create();

    try (Reader reader = new FileReader(file)) {
        Type playlistType = new TypeToken<Playlist>() {}.getType();
        Playlist playlist = gson.fromJson(reader, playlistType);

        // 💡 Rellenar metadatos de cada canción
        for (Song song : playlist.getSongs()) {
            File songFile = song.getFile();
            if (songFile != null && songFile.exists()) {
                song.setMetadata(DOMAIN.MetadataExtractor.extractMetadata(songFile));
                System.out.println("METADATA RESTAURADA: " + song.getName() + " -> " + song.getMetadata().getCoverImagePath());
            } else {
                System.out.println("ARCHIVO NO ENCONTRADO: " + song.getFilePath());
            }
        }

        return playlist;
    }
    }
    
    private String sanitizeFileName(String input) {
        return input.replaceAll("[\\\\/:*?\"<>|]", "_");
    }
}
