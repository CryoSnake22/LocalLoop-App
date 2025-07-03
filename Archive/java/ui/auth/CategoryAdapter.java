package com.example.localloop.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localloop.R;
import com.example.localloop.data.model.Category;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final List<Category> categoryList;
    private final Context context;

    public CategoryAdapter(List<Category> categoryList, Context context) {
        this.categoryList = categoryList;
        this.context = context;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);

        holder.tvCategoryName.setText(category.getName());
        holder.tvCategoryDescription.setText(category.getDescription());

        holder.btnCategoryEdit.setOnClickListener(v -> openEditActivity(category));

        holder.btnCategoryDelete.setOnClickListener(v -> deleteCategory(category, position));
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    /**
     * Launches EditCategoryActivity with the current category data.
     */
    private void openEditActivity(Category category) {
        Intent intent = new Intent(context, EditCategoryActivity.class);
        intent.putExtra("id", category.getId());
        intent.putExtra("name", category.getName());
        intent.putExtra("description", category.getDescription());
        context.startActivity(intent);
    }

    /**
     * Deletes the category from Firestore and updates the adapter.
     */
    private void deleteCategory(Category category, int position) {
        FirebaseFirestore.getInstance()
                .collection("categories")
                .document(category.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Category deleted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to delete category", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * ViewHolder for Category items.
     */
    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName, tvCategoryDescription;
        Button btnCategoryEdit, btnCategoryDelete;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvCategoryDescription = itemView.findViewById(R.id.tvCategoryDescription);
            btnCategoryEdit = itemView.findViewById(R.id.btnCategoryEdit);
            btnCategoryDelete = itemView.findViewById(R.id.btnCategoryDelete);
        }
    }
}
