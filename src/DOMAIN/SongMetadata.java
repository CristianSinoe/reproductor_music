/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DOMAIN;

/**
 *
 * @author sinoe
 */
public class SongMetadata {
    private String artist;
    private String album;
    private int year;

    public SongMetadata(String artist, String album, int year) {
        this.artist = artist;
        this.album = album;
        this.year = year;
    }

    // Getters y setters
    public String getArtist() { return artist; }
    public String getAlbum() { return album; }
    public int getYear() { return year; }

    @Override
    public String toString() {
        return artist + " - " + album + " (" + year + ")";
    }
}

