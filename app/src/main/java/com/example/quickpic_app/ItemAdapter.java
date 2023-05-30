package com.example.quickpic_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickpic_app.models.Product;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<Product> items;
    private SparseBooleanArray selectedItems;

    public ItemAdapter(List<Product> items) {
        this.items = items;
        this.selectedItems = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        return new ViewHolder(view);
    }

// To handle the empty list text visibility in your Layout
    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private StateListDrawable getItemBackground(Context context) {
        StateListDrawable states = new StateListDrawable();

        Drawable normalDrawable = ContextCompat.getDrawable(context, R.drawable.item_border);
        Drawable selectedDrawable = ContextCompat.getDrawable(context, R.drawable.item_border_selected);

        states.addState(new int[]{android.R.attr.state_selected}, selectedDrawable);
        states.addState(new int[]{}, normalDrawable);

        return states;
    }

    public void toggleSelection(int position) {
        if (selectedItems.get(position, false)) {
            selectedItems.delete(position);
        } else {
            selectedItems.put(position, true);
        }
        notifyItemChanged(position);
    }

    public void clearSelection() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Product> getSelectedItems() {
        List<Product> selectedProducts = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            selectedProducts.add(items.get(selectedItems.keyAt(i)));
        }
        return selectedProducts;
    }

    public void removeItem(Product product) {
        int position = items.indexOf(product);
        if (position != -1) {
            items.remove(position);
            notifyItemRemoved(position);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product item = items.get(position);
        holder.itemName.setText(item.getName());
        holder.itemImage.setImageBitmap(item.getImage());

        if (selectedItems.get(position, false)) {
            holder.itemView.setSelected(true);
        } else {
            holder.itemView.setSelected(false);
        }
        holder.itemView.setBackground(getItemBackground(holder.itemView.getContext()));
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        ImageView itemImage;
        TextView itemName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            itemName = itemView.findViewById(R.id.item_name);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (getSelectedItemCount() > 0) {
                toggleSelection(position);
            }
//            if (position != RecyclerView.NO_POSITION) {
                else { Product selectedItem = items.get(position);
                Intent intent = new Intent(itemView.getContext(), ItemDetailsActivity.class); // Corrected line
                intent.putExtra("selected_item", selectedItem);
                itemView.getContext().startActivity(intent); // Corrected line
            }
        }

        @Override
        public boolean onLongClick(View v) {
            int position = getAdapterPosition();
            toggleSelection(position);
            return true;
        }
    }
}
