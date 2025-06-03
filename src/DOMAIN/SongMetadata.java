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
    private String coverImagePath;

    public SongMetadata(String artist, String album, int year, String coverImagePath) {
        this.artist = artist;
        this.album = album;
        this.year = year;
        this.coverImagePath = coverImagePath;
    }
    
    public SongMetadata() {
    }

    public String getArtist() { 
        return artist;
    }
    
    public void setArtist(String artist) {
        this.artist = artist;
    }
    
    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }
    
    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getCoverImagePath() {
        return coverImagePath;
    }

    public void setCoverImagePath(String coverImagePath) {
        this.coverImagePath = coverImagePath;
    }

    @Override
    public String toString() {
        return artist + " - " + album + " (" + year + ")";
    }
}
