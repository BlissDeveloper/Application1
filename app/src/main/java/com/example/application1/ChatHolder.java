package com.example.application1;

import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class ChatHolder extends RecyclerView.ViewHolder {
    private FirebaseAuth mAuth;
    private String currentUserID;

    public TextView textViewOtherChat;
    public TextView textViewInitials;
    public TextView textViewOtherDate;
    public TextView textViewYourChat;
    public TextView textViewYourDate;

    public CardView cardViewOtherChat;
    public CardView cardViewYourChat;

    public ChatHolder(@NonNull View itemView) {
        super(itemView);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        textViewOtherChat = itemView.findViewById(R.id.textViewOtherMessage);
        textViewInitials = itemView.findViewById(R.id.textViewChatInitials);
        textViewOtherDate = itemView.findViewById(R.id.textViewOtherDate);
        textViewYourChat = itemView.findViewById(R.id.textViewYourChat);
        textViewYourDate = itemView.findViewById(R.id.textViewYourDate);

        cardViewYourChat = itemView.findViewById(R.id.cardViewYourChat);
        cardViewOtherChat = itemView.findViewById(R.id.cardViewOtherChat);

        cardViewOtherChat.setVisibility(View.GONE);
        cardViewYourChat.setVisibility(View.GONE);
    }

    public void setUser_id(String user_id, Chat chat) {
        if (user_id.equals(currentUserID)) {
            //Chat mo
            cardViewOtherChat.setVisibility(View.GONE);
            cardViewYourChat.setVisibility(View.VISIBLE);

            textViewYourChat.setVisibility(View.VISIBLE);
            textViewYourDate.setVisibility(View.VISIBLE);

            textViewYourChat.setText(chat.getMessage());
            textViewYourDate.setText(chat.getTime());
            textViewInitials.setVisibility(View.GONE);
        } else {
            //Chat ng iba
            cardViewOtherChat.setVisibility(View.VISIBLE);
            cardViewYourChat.setVisibility(View.GONE);

            textViewYourChat.setVisibility(View.GONE);
            textViewYourDate.setVisibility(View.GONE);
            textViewInitials.setText(chat.getInitials());

            textViewOtherChat.setText(chat.getMessage());
            textViewOtherDate.setText(chat.getTime());
        }
    }
}
