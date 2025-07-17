package com.example.prm392mnlv.ui.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;

import com.example.prm392mnlv.R;
import com.example.prm392mnlv.data.models.User;
import com.example.prm392mnlv.ui.activities.ChatActivity;

import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {

    private final List<User> members;
    private final Context context;

    public MemberAdapter(List<User> members, Context context) {
        this.members = members;
        this.context = context;
    }

    @NonNull
    @Override
    public MemberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberAdapter.ViewHolder holder, int position) {
        User member = members.get(position);
        holder.txtMemberName.setText(member.getName());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("receiverId", member.getId()); // Member ID là người nhận
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtMemberName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMemberName = itemView.findViewById(R.id.txtMemberName);
        }
    }
}