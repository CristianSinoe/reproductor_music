/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LOGIC;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sinoe
 */
public class PlaylistManager {
    private final File dir;

    public PlaylistManager(File dir) {
        this.dir = dir;
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (created) {
                System.out.println("DIRECTORIO CREADO EN: " + dir.getAbsolutePath());
            } else {
                System.out.println("NO SE PUDO CREAR EL DIRECTORIO DE PLAYLIST O YA EXISTE");
            }
        }
    }

    public List<Playlist> loadAll() throws IOException {
    List<Playlist> playlists = new ArrayList<>();
    File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
    if (files != null) {
        for (File f : files) {
            try {
                Playlist p = Playlist.loadFromFile(f);
                if (p != null) playlists.add(p);
            } catch (Exception e) {
                System.err.println("ERROR CARGANDO PLAYLIST DESDE " + f.getName() + ": " + e.getMessage());
            }
        }
    }
    return playlists;
}


    public void savePlaylist(Playlist playlist) throws IOException {
        playlist.saveToFile(dir);
    }

    public void deletePlaylist(Playlist playlist) {
        File file = new File(dir, playlist.getName() + ".json");
        if (file.exists()) file.delete();
    }

    public void renamePlaylist(Playlist playlist, String newName) throws IOException {
        File oldFile = new File(dir, playlist.getName() + ".json");
        File newFile = new File(dir, newName + ".json");
        if (oldFile.exists()) oldFile.renameTo(newFile);
        playlist.setName(newName);
        savePlaylist(playlist);
    }
}

