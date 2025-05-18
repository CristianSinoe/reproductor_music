/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DOMAIN;

import java.io.File;

/**
 *
 * @author sinoe
 */
public class Song {
    private String name;
    private String filePath;         // Guardamos la ruta en texto para JSON
    private transient File file;     // Marcado como transient para que Gson no lo serialice
    private SongMetadata metadata;

    // Constructor principal
    public Song(String name, File file, SongMetadata metadata) {
        this.name = name;
        this.file = file;
        this.filePath = file.getAbsolutePath();
        this.metadata = metadata;
    }

    // Constructor vacío para Gson
    public Song() {
    }

    // Constructor alternativo con ruta como String (útil al deserializar)
    public Song(String name, String filePath, SongMetadata metadata) {
        this.name = name;
        this.filePath = filePath;
        this.file = new File(filePath);
        this.metadata = metadata;
    }

    public String getName() {
        return name;
    }

    public File getFile() {
        if (file == null && filePath != null) {
            file = new File(filePath);
        }
        return file;
    }

    public String getFilePath() {
        return filePath;
    }

    public SongMetadata getMetadata() {
        return metadata;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
        this.file = new File(filePath);
    }

    public void setFile(File file) {
        this.file = file;
        this.filePath = file.getAbsolutePath();
    }

    public void setMetadata(SongMetadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return name + " (" + (metadata != null ? metadata.toString() : "Sin metadata") + ")";
    }
}