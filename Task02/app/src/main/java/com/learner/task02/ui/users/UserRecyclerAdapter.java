package com.learner.task02.ui.users;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.learner.task02.R;
import com.learner.task02.data.model.User;
import com.learner.task02.databinding.UserRowBinding;

import java.util.ArrayList;
import java.util.List;

public class UserRecyclerAdapter extends RecyclerView.Adapter<UserRecyclerAdapter.UserHolder> {
    private List<User> userList = new ArrayList<>();

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        UserRowBinding rowBinding = DataBindingUtil.inflate(inflater, R.layout.user_row, parent, false);
        return new UserHolder(rowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        User user = userList.get(position);
        holder.userRowBinding.setUser(user);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }

    class UserHolder extends RecyclerView.ViewHolder {
        private UserRowBinding userRowBinding;

        public UserHolder(@NonNull UserRowBinding userRowBinding) {
            super(userRowBinding.getRoot());
            this.userRowBinding = userRowBinding;
        }
    }
}
