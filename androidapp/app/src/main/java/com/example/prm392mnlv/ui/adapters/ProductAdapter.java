package com.example.prm392mnlv.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prm392mnlv.R;
import com.example.prm392mnlv.data.models.Product;
import com.example.prm392mnlv.ui.activities.ProductDetailsActivity;
import com.example.prm392mnlv.util.TextUtils;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private Context context;
    private List<Product> productList;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate item_product.xml
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // Gán dữ liệu cho các view
        holder.tvProductName.setText(product.getProductName());
        holder.tvPrice.setText(TextUtils.formatPrice(product.getPrice()));

        // Load hình ảnh với Glide
        Glide.with(context)
                .load(product.getImageUrl().toString())
                .placeholder(R.drawable.placeholder_product)
                .into(holder.ivProductImage);

        View.OnClickListener toProductDetails = v -> {
            Intent intent = new Intent(context, ProductDetailsActivity.class);
            intent.putExtra("productId", product.getId());
            intent.putExtra("productName", product.getProductName());
            intent.putExtra("description", product.getDescription());
            intent.putExtra("price", product.getPrice());
            intent.putExtra("quantityInStock", product.getQuantityInStock());
            intent.putExtra("imageUrl", product.getImageUrl().toString());
            intent.putExtra("categoryName", product.getCategoryName());
            context.startActivity(intent);
        };

        holder.btnViewDetails.setOnClickListener(toProductDetails);
        holder.itemView.setOnClickListener(toProductDetails);
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    // ViewHolder chứa các view trong item_product.xml
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvPrice;
        Button btnViewDetails;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.productImage);
            tvProductName = itemView.findViewById(R.id.productName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnViewDetails = itemView.findViewById(R.id.btnAddToCart);
        }
    }
}
