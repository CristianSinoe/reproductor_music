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
    private transient File file;   
    private String filePath;   
    private SongMetadata metadata;
    private int durationMillis = 0; // Duración en milisegundos

    public Song(String name, File file, SongMetadata metadata) {
        this.name = name;
        this.file = file;
        this.filePath = file != null ? file.getAbsolutePath() : null;
        this.metadata = metadata;
    }

    public Song() {}

    public Song(String artist, String album, int year, String coverImagePath, String filePath) {
        this.metadata = new SongMetadata(artist, album, year, coverImagePath);
        this.filePath = filePath;
        this.name = new File(filePath).getName();
    }

    // Nuevo getter y setter para duración
    public int getDurationMillis() {
        return durationMillis;
    }

    public void setDurationMillis(int durationMillis) {
        this.durationMillis = durationMillis;
    }

    // Getters y setters existentes
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getFile() {
        if (file == null && filePath != null) {
            file = new File(filePath);
        }
        return file;
    }

    public void setFile(File file) {
        this.file = file;
        this.filePath = file != null ? file.getAbsolutePath() : null;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
        this.file = filePath != null ? new File(filePath) : null;
    }

    public SongMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(SongMetadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return name + " (" + (metadata != null ? metadata.toString() : "SIN METADATA") + ")";
    }
}
