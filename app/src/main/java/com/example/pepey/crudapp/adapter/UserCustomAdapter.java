package com.example.pepey.crudapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.pepey.crudapp.R;

import java.util.List;

/**
 * Created by pepey on 9/24/17.
 */

public class UserCustomAdapter extends RecyclerView.Adapter<UserCustomAdapter.ViewHolder> {
    private List<User> userList;
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvNama, tvNis;
        public ViewHolder(View itemView) {
            super(itemView);
            tvNama  = itemView.findViewById(R.id.tvNama);
            tvNis   = itemView.findViewById(R.id.tvNis);
        }
    }
    public UserCustomAdapter (List<User> listUser){
        this.userList = listUser;
    }

    @Override
    public UserCustomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user, parent, false);
        return new ViewHolder(view);
    }

    public void add(User user){
        userList.add(user);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(UserCustomAdapter.ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.tvNama.setText(user.getNama());
        holder.tvNis.setText(user.getNis());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
