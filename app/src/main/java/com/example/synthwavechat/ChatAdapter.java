package com.example.synthwavechat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder>{

    private Context context;
    private List<ChatMessage> messages;

    public ChatAdapter(Context context, List<ChatMessage> messages)
    {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position)
    {
        ChatMessage message = messages.get(position);

        // Nickname y mensajes
        holder.tvNickname.setText(message.getNickname());
        holder.tvMessage.setText(message.getMessage());

        // carga la imagen de perfil con el glide
        Glide.with(context)
                .load(message.getProfilePictureUrl())
                .placeholder(R.drawable.synthwave)
                .into(holder.ivProfilePicture);
    }

    @Override
    public int getItemCount()
    {
        return messages.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder
    {
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
}
