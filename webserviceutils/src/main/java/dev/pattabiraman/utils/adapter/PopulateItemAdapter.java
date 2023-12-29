package dev.pattabiraman.utils.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import dev.pattabiraman.utils.callback.OnItemClicked;
import dev.pattabiraman.utils.model.ItemModel;
import dev.pattabiraman.webserviceutils.R;
import dev.pattabiraman.webserviceutils.databinding.InflateBottomSheetRecyclerviewRowItemBinding;

public class PopulateItemAdapter extends RecyclerView.Adapter<PopulateItemAdapter.ViewHolder> implements Filterable {
    private final LayoutInflater layoutInflater;
    private final AppCompatActivity activity;
    private final OnItemClicked onItemClicked;
    private final ArrayList<ItemModel> itemModelArrayList;
    final boolean isToShowArrowAtEnd;
    private ArrayList<ItemModel> filteredItemModelArrayList;

    public String getSelectedLanguageCode() {
        return selectedLanguageCode;
    }

    public void setSelectedLanguageCode(String selectedLanguageCode) {
        this.selectedLanguageCode = selectedLanguageCode;
    }

    private String selectedLanguageCode = "or";

    /**
     * @param activity           AppCompatActivity object of calling class
     * @param itemModelArrayList ArrayList of ItemModel class to populate data
     * @param isToShowArrowAtEnd whether to show arrow at the end of row item
     * @param onItemClicked      callback when RecyclerView row item is clicked
     * @apiNote Mostly used to customize BottomSheetDialog.<br/><br/>
     * • Possible to send HTML embedded styles for ItemModel.title (displayed using Html.fromHtml(ItemModel.title)<br/><br/>
     * • If ItemModel.iconResId is NOT set, ItemModel.title will display as Gravity.CENTER alignment with no icon on the left to every row item.<br/><br/>
     * • If ItemModel.iconResId is set, ItemModel.title will display as Gravity.LEFT alignment with mentioned resource icon displayed on the left side of every row item.
     */
    public PopulateItemAdapter(AppCompatActivity activity, ArrayList<ItemModel> itemModelArrayList, final boolean isToShowArrowAtEnd, OnItemClicked onItemClicked) {
        this.activity = activity;
        this.itemModelArrayList = itemModelArrayList;
        this.isToShowArrowAtEnd = isToShowArrowAtEnd;
        this.filteredItemModelArrayList = itemModelArrayList;
        this.onItemClicked = onItemClicked;
        this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.selectedLanguageCode = getSelectedLanguageCode();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        InflateBottomSheetRecyclerviewRowItemBinding binding = InflateBottomSheetRecyclerviewRowItemBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(binding, getItemViewType(viewType));
    }

    @SuppressLint("RtlHardcoded")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ItemModel itemModel = filteredItemModelArrayList.get(position);
        holder.binding.tvTitle.setTag(itemModel);

        if (!TextUtils.isEmpty(itemModel.getDescription()) && this.selectedLanguageCode.equalsIgnoreCase("or")) {
            holder.binding.tvTitle.setText(Html.fromHtml(itemModel.getDescription()));
        } else {
            holder.binding.tvTitle.setText(Html.fromHtml(itemModel.getTitle()));
        }

        if (isToShowArrowAtEnd) {
            holder.binding.tvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(activity, R.drawable.round_arrow_forward_ios_24), null);
        } else {
            holder.binding.tvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }

        if (itemModel.getIconResId() == 0) {
            holder.binding.ivResIcon.setVisibility(View.GONE);
            holder.binding.tvTitle.setGravity(Gravity.CENTER);
        } else {
            holder.binding.tvTitle.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            holder.binding.ivResIcon.setVisibility(View.VISIBLE);
            holder.binding.ivResIcon.setColorFilter(ContextCompat.getColor(activity, dev.pattabiraman.webserviceutils.R.color.secondaryPink));
            holder.binding.ivResIcon.setImageResource(itemModel.getIconResId());
        }

        holder.binding.llLanguageItemParent.setOnClickListener(v -> {
            final ItemModel selectedItemModel = (ItemModel) holder.binding.tvTitle.getTag();
            onItemClicked.onItemClicked(selectedItemModel);
        });
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return filteredItemModelArrayList.size();
    }


    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filterPattern = constraint.toString().toLowerCase().trim();
                FilterResults results = new FilterResults();

                if (filterPattern.isEmpty()) {
                    results.values = itemModelArrayList;
                    results.count = itemModelArrayList.size();
                } else {
                    List<ItemModel> filteredItems = new ArrayList<>();

                    for (ItemModel item : itemModelArrayList) {
                        if (item.getTitle().toLowerCase().contains(filterPattern)) {
                            filteredItems.add(item);
                        }
                    }

                    results.values = filteredItems;
                    results.count = filteredItems.size();
                }

                return results;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredItemModelArrayList = (ArrayList<ItemModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        InflateBottomSheetRecyclerviewRowItemBinding binding;
        int itemViewType;

        ViewHolder(InflateBottomSheetRecyclerviewRowItemBinding binding, int itemViewType) {
            super(binding.getRoot());
            this.binding = binding;
            this.itemViewType = itemViewType;
        }
    }
}
