package com.example.localloop.ui.auth;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localloop.R;
import com.example.localloop.data.model.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    List<User> userList ;
    private Context context;

    public UserAdapter(List<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public UserAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user,parent,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
        User user = userList.get(position);
        String fullName = user.getFirstName()+" "+user.getLastName();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        holder.tvUserName.setText(fullName);
        holder.tvUserEmail.setText(user.getEmail());
        holder.tvUserRole.setText(user.getRole());
        if (!user.isActive()){
            holder.tvUserDisabled.setVisibility(View.VISIBLE);
            holder.userCard.setCardBackgroundColor(Color.GRAY);
            holder.btnUserDisable.setText("Enable");
        }
        else{
            holder.tvUserDisabled.setVisibility(View.GONE);
            holder.userCard.setCardBackgroundColor(Color.WHITE);
            holder.btnUserDisable.setText("Disable");
        }
        holder.btnUserDisable.setOnClickListener(v->{
            boolean activity = !user.isActive();
            user.setActive(activity);
            FirebaseFirestore.getInstance()
                    .collection("user_db")
                    .document(user.getUID())
                    .update("active", activity)
                    .addOnSuccessListener(a->{
                       notifyItemChanged(position);
                    });
        });
        holder.btnUserDelete.setOnClickListener(v->{
            FirebaseFirestore.getInstance()
                    .collection("user_db")
                    .document(user.getUID())
                    .delete()
                    .addOnSuccessListener(a->{
                        notifyItemRemoved(position);
                    });
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder{
        TextView tvUserName, tvUserEmail, tvUserRole ,tvUserDisabled;

        Button btnUserDelete, btnUserDisable;
        CardView userCard;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            tvUserRole = itemView.findViewById(R.id.tvUserRole);
            btnUserDelete=itemView.findViewById(R.id.btnUserDelete);
            btnUserDisable=itemView.findViewById(R.id.btnUserDisable);
            userCard = itemView.findViewById(R.id.userCard);
        }
    }
}
