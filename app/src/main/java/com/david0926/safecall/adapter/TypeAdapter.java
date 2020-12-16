package com.david0926.safecall.adapter;

import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.david0926.safecall.R;
import com.david0926.safecall.databinding.RowTypeBinding;
import com.david0926.safecall.model.TypeModel;

import java.util.ArrayList;
import java.util.List;

public class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.TypeHolder> {

    private List<TypeModel> list;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, TypeModel item);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(View view, TypeModel item);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }

    public TypeAdapter() {
        this.list = new ArrayList<>();
    }

    @NonNull
    @Override
    public TypeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TypeHolder(RowTypeBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TypeHolder holder, int position) {
        TypeModel item = list.get(position);
        holder.bind(item, mOnItemClickListener, mOnItemLongClickListener);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void setItem(List<TypeModel> items) {
        this.list = items;
        notifyDataSetChanged();
    }

    static class TypeHolder extends RecyclerView.ViewHolder {

        RowTypeBinding binding;

        MediaPlayer mediaPlayer;

        TypeHolder(RowTypeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(TypeModel item, OnItemClickListener listener, OnItemLongClickListener longListener) {
            binding.setItem(item);
            binding.setIsPlaying(false);

            //bad code bad design pattern bad developer
            binding.btnRowTypePlay.setOnClickListener(view -> {
                if (!binding.getIsPlaying()) {
                    binding.setIsPlaying(true);

                    mediaPlayer = MediaPlayer.create(view.getContext(), item.getVoice().contains("남성")
                            ? R.raw.response : R.raw.response2);
                    mediaPlayer.start();
                    mediaPlayer.setOnCompletionListener(mediaPlayer1 -> binding.setIsPlaying(false));
                } else {
                    mediaPlayer.stop();
                    mediaPlayer = null;
                    binding.setIsPlaying(false);
                }
            });

            itemView.setOnClickListener(view -> listener.onItemClick(view, item));
            itemView.setOnLongClickListener(view -> longListener.onItemLongClick(view, item));
        }
    }
}

