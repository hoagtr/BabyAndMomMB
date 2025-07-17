package com.example.prm392mnlv.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392mnlv.R;
import com.example.prm392mnlv.data.models.MenuItem;
import com.example.prm392mnlv.ui.activities.CartActivity;
import com.example.prm392mnlv.ui.activities.ChatActivity;
import com.example.prm392mnlv.ui.activities.LogoutActivity;
import com.example.prm392mnlv.ui.activities.MapActivity;
import com.example.prm392mnlv.util.TokenHelper;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {
    private final List<MenuItem> list;
    private final Context context;

    public MenuAdapter(List<MenuItem> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_menu, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        MenuItem item = list.get(position);
        holder.label.setText(item.title);
        holder.icon.setImageResource(item.icon);

        holder.itemView.setOnClickListener(v -> {
            switch (item.title) {
                case "Cart":
                    context.startActivity(new Intent(context, CartActivity.class));
                    break;
                case "Logout":
                    context.startActivity(new Intent(context, LogoutActivity.class));
                    break;
                case "Map":
                    context.startActivity(new Intent(context, MapActivity.class));
                    break;
                case "Chat":
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("receiverId", "7423B0F5-D9E5-42E6-79B0-08DD644E8FA6");
                        context.startActivity(intent);
                    break;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView label;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.menuIcon);
            label = itemView.findViewById(R.id.menuLabel);
        }
    }
}
