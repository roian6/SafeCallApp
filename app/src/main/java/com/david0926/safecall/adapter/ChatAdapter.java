package com.david0926.safecall.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.david0926.safecall.databinding.RowChatBinding;
import com.david0926.safecall.model.ChatModel;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatHolder> {

    private List<ChatModel> list;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, ChatModel item);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(View view, ChatModel item);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }

    public ChatAdapter() {
        this.list = new ArrayList<>();
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewChat) {
        return new ChatHolder(RowChatBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
        ChatModel item = list.get(position);
        holder.bind(item, mOnItemClickListener, mOnItemLongClickListener);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void setItem(List<ChatModel> items) {
        this.list = items;
        notifyDataSetChanged();
    }

    static class ChatHolder extends RecyclerView.ViewHolder {

        RowChatBinding binding;

        ChatHolder(RowChatBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ChatModel item, OnItemClickListener listener, OnItemLongClickListener longListener) {
            binding.setItem(item);
            itemView.setOnClickListener(view -> listener.onItemClick(view, item));
            itemView.setOnLongClickListener(view -> longListener.onItemLongClick(view, item));
        }
    }
}

