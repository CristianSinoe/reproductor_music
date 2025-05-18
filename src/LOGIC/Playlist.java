/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LOGIC;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import DOMAIN.Song;

import java.io.*;
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

    // Constructor vacío para Gson
    public Playlist() {}

    public Playlist(String name) {
        this.name = name;
    }

    // Getters y setters
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

    // Guarda playlist en archivo JSON
    public void saveToFile(File dir) throws IOException {
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, sanitizeFileName(name) + ".json");
        Gson gson = new Gson();
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(this, writer);
        }
    }

    // Carga playlist desde archivo JSON
    public static Playlist loadFromFile(File file) throws IOException {
        Gson gson = new Gson();
        try (Reader reader = new FileReader(file)) {
            Type playlistType = new TypeToken<Playlist>() {}.getType();
            return gson.fromJson(reader, playlistType);
        }
    }

    // Evita caracteres inválidos en el nombre para el archivo
    private String sanitizeFileName(String input) {
        return input.replaceAll("[\\\\/:*?\"<>|]", "_");
    }
}