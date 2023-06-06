package ru.samsung.itschool.mdev.authfirebase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.samsung.itschool.mdev.authfirebase.databinding.ListItemBinding;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.CustomViewHolder>{

    private ArrayList<String> mList;
    private Context mContext;
    private ListItemBinding binding;

    private static final int[] NAV_ACTIONS = new int[]{
            R.id.action_google,
            R.id.action_emailpassword,
            R.id.action_phoneauth
    };

    public MyAdapter(ArrayList<String> mList, Context mContext) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public MyAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        binding = ListItemBinding.inflate(inflater,parent,false);
        return new CustomViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.CustomViewHolder holder, int position) {
        binding.tvAction.setText(mList.get(position));
        int destination = NAV_ACTIONS[position];
        holder.itemView.setOnClickListener(Navigation.createNavigateOnClickListener(destination));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        public CustomViewHolder(ListItemBinding itemBinding) {
            super(itemBinding.getRoot());
        }
    }

}