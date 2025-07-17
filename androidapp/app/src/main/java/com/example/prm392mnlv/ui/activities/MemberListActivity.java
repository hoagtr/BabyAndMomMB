package com.example.prm392mnlv.ui.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392mnlv.R;
import com.example.prm392mnlv.data.models.MenuItem;
import com.example.prm392mnlv.data.models.User;
import com.example.prm392mnlv.ui.adapters.MemberAdapter;
import com.example.prm392mnlv.ui.adapters.MenuAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MemberListActivity extends AppCompatActivity {

    private RecyclerView menuRecyclerView;
    private RecyclerView recyclerView;
    private List<User> memberList = new ArrayList<>();
    private MemberAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_list);

        menuRecyclerView = findViewById(R.id.menuRecyclerView);
        setupMenu();

        recyclerView = findViewById(R.id.recyclerViewMembers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MemberAdapter(memberList, this);
        recyclerView.setAdapter(adapter);

        fetchMembers();
    }

    private void fetchMembers() {
        // Ví dụ dùng Firebase
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.orderByChild("role").equalTo("Member")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        memberList.clear();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            User user = data.getValue(User.class);
                            memberList.add(user);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MemberListActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupMenu() {
        menuRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        List<MenuItem> menuList = Arrays.asList(
                new MenuItem("Map", R.drawable.ic_map),
                new MenuItem("Logout", R.drawable.ic_logout)
        );

        MenuAdapter adapter = new MenuAdapter(menuList, this);
        menuRecyclerView.setAdapter(adapter);
    }
}
