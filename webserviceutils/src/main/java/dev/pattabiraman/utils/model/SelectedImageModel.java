/*
 * Created by Pattabi Raman on 03/05/23, 2:08 PM
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 02/05/23, 3:11 PM
 */

package dev.pattabiraman.utils.model;

import android.graphics.Bitmap;
import android.net.Uri;

/**
  * The SelectedImageModel class represents a model for storing information about a selected image,
  * including its path, URI, date taken, thumbnail, size, and location.
  */
 public class SelectedImageModel {
    private String pathOfSelectedImage;
    private Uri uriOfImage;
    private String DATE_TAKEN;
    private boolean thumbnailsselection;
    private String arrPath;
    private int selectedBgColor;
    private int drawableCheckbox;
    private long selectedImageTimeInMillis;

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

    public long getSelectedImageTimeInMillis() {
        return selectedImageTimeInMillis;
    }

    public void setSelectedImageTimeInMillis(long selectedImageTimeInMillis) {
        this.selectedImageTimeInMillis = selectedImageTimeInMillis;
    }
}
