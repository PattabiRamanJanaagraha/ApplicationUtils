package dev.pattabiraman.utils.callback;

public interface OnItemClicked {
    /**
     * This function is called when an item is clicked and takes in the clicked item's model object as
     * a parameter.
     *
     * @param clickedItemModelObject The parameter "clickedItemModelObject" is an object that
     * represents the model data of the item that was clicked. It could be any type of object depending
     * on the context of the application or program. For example, if the item clicked is a product in
     * an e-commerce app, the "clickedItem
     */
    void onItemClicked(Object clickedItemModelObject);
}
