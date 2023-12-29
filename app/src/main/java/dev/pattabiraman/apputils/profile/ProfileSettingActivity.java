package dev.pattabiraman.apputils.profile;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import dev.pattabiraman.apputils.R;
import dev.pattabiraman.apputils.databinding.ActivityProfileSettingBinding;
import dev.pattabiraman.utils.PluginAppUtils;
import dev.pattabiraman.utils.adapter.PopulateItemAdapter;
import dev.pattabiraman.utils.callback.HandleBackgroundThread;
import dev.pattabiraman.utils.callback.HandlerUtils;
import dev.pattabiraman.utils.callback.OnItemClicked;
import dev.pattabiraman.utils.model.ItemModel;
import dev.pattabiraman.utils.permissionutils.PluginBaseAppCompatActivity;

public class ProfileSettingActivity extends PluginBaseAppCompatActivity {
    AppCompatActivity activity;
     ActivityProfileSettingBinding binding;
    ArrayList<ItemModel> itemModelArrayList = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileSettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = ProfileSettingActivity.this;

        populateProfileSettingData();
    }

    /**
     * The function "populateProfileSettingData()" populates a list of profile setting options with
     * corresponding icons and handles the click events for each option.
     */
    private void populateProfileSettingData() {
        itemModelArrayList.clear();
        new HandlerUtils().handleAsyncOperation(activity, new HandleBackgroundThread() {
            @Override
            public void handleDoInBackground() {
                ItemModel itemModel = new ItemModel(1, "Change Language", R.drawable.ic_launcher_foreground);
                itemModelArrayList.add(itemModel);

                itemModel = new ItemModel(2, "Rate us on play store", R.drawable.ic_launcher_foreground);
                itemModelArrayList.add(itemModel);

                itemModel = new ItemModel(3, "Privacy Policy", R.drawable.ic_launcher_foreground);
                itemModelArrayList.add(itemModel);

                itemModel = new ItemModel(4, "Logout", R.drawable.ic_launcher_foreground);
                itemModelArrayList.add(itemModel);
            }

            @Override
            public void handlePostExecute() {
                PluginAppUtils.getInstance(activity).setRecyclerViewLayoutManager(activity, binding.rvProfileOptions, RecyclerView.VERTICAL, true);
                binding.rvProfileOptions.setAdapter(new PopulateItemAdapter(activity, itemModelArrayList, true, new OnItemClicked() {
                    @Override
                    public void onItemClicked(Object clickedItemModelObject) {
                        PluginAppUtils.getInstance(activity).showToast(activity, ((ItemModel) clickedItemModelObject).getTitle());
                    }
                }));
            }

            @Override
            public void handleException(@Nullable String exceptionMessage) {

            }
        });
    }
}
