package dev.pattabiraman.utils.model;

import org.json.JSONObject;

public class ItemModel {
    private String title, code, description;
    private int iconResId, itemId;
    private JSONObject itemDataAsJSONObject;

    public ItemModel(final int itemId, String languageTitle, String languageCode, String description) {
        this.itemId = itemId;
        this.title = languageTitle;
        this.code = languageCode;
        this.description = description;
    }

    public ItemModel() {
    }

    public ItemModel(final int itemId, String languageTitle, String languageCode) {
        this.itemId = itemId;
        this.title = languageTitle;
        this.code = languageCode;
    }

    public ItemModel(final int itemId, String languageTitle, int iconResId) {
        this.itemId = itemId;
        this.title = languageTitle;
        this.iconResId = iconResId;
    }

    public ItemModel(final int itemId, String languageTitle) {
        this.itemId = itemId;
        this.title = languageTitle;
    }

    public String getTitle() {
        return title;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public int getIconResId() {
        return iconResId;
    }

    public int getItemId() {
        return itemId;
    }

    /**
     * The function returns the item data as a JSONObject.
     *
     * @return The method is returning a JSONObject.
     */
    public JSONObject getItemDataAsJSONObject() {
        return itemDataAsJSONObject;
    }

    /**
     * The function sets the itemDataAsJSONObject property of an ItemModel object and returns the
     * updated object.
     *
     * @param itemDataAsJSONObject The parameter "itemDataAsJSONObject" is a JSONObject that represents
     * the data of an item.
     * @return The method is returning an instance of the ItemModel class.
     */
    public ItemModel setItemDataAsJSONObject(JSONObject itemDataAsJSONObject) {
        this.itemDataAsJSONObject = itemDataAsJSONObject;
        return this;
    }
}

