package dev.pattabiraman.webserviceutils.model;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by pattabi.raman on 22-08-2017.
 */

public class SelectedImageModel {
    private String pathOfSelectedImage;
    private Uri uriOfImage;
    private String DATE_TAKEN;
    private boolean thumbnailsselection;
    private String arrPath;
    private int selectedBgColor;
    private int drawableCheckbox;

    public boolean isThumbnailsselection() {
        return thumbnailsselection;
    }

    public void setThumbnailsselection(boolean thumbnailsselection) {
        this.thumbnailsselection = thumbnailsselection;
    }

    public int getSelectedBgColor() {
        return selectedBgColor;
    }

    public void setSelectedBgColor(int selectedBgColor) {
        this.selectedBgColor = selectedBgColor;
    }

    public int getDrawableCheckbox() {
        return drawableCheckbox;
    }

    public void setDrawableCheckbox(int drawableCheckbox) {
        this.drawableCheckbox = drawableCheckbox;
    }

    public String getArrPath() {

        return arrPath;
    }

    public void setArrPath(String arrPath) {
        this.arrPath = arrPath;
    }


    public Bitmap getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(Bitmap thumbnails) {
        this.thumbnails = thumbnails;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    private double latitude;
    private Bitmap thumbnails;
    private long id;

    public int getSizeInMB() {
        return sizeInMB;
    }

    public void setSizeInMB(int sizeInMB) {
        this.sizeInMB = sizeInMB;
    }

    private int sizeInMB;
    private double longitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    /**
     * @return the dATE_TAKEN
     */
    public String getDATE_TAKEN() {
        return DATE_TAKEN;
    }

    /**
     * @param dATE_TAKEN the dATE_TAKEN to set
     */
    public void setDATE_TAKEN(String dATE_TAKEN) {
        DATE_TAKEN = dATE_TAKEN;
    }


    public String getPathOfSelectedImage() {
        return pathOfSelectedImage;
    }

    public void setPathOfSelectedImage(String pathOfSelectedImage) {
        this.pathOfSelectedImage = pathOfSelectedImage;
    }

    public Uri getUriOfImage() {
        return uriOfImage;
    }

    public void setUriOfImage(Uri uriOfImage) {
        this.uriOfImage = uriOfImage;
    }



}
