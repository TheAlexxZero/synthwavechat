package com.example.synthwavechat;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatViewHolder extends RecyclerView.ViewHolder {
    TextView tvNickname, tvMessage;
    ImageView ivProfilePicture;

    public ChatViewHolder(@NonNull View itemView)
    {
        super(itemView);
        tvNickname = itemView.findViewById(R.id.tvNickname);
        tvMessage = itemView.findViewById(R.id.tvMessage);
        ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
    }

}
