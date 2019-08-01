package com.example.application1.Fragment;


import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.application1.Chat;
import com.example.application1.ChatHolder;
import com.example.application1.DateUtils;
import com.example.application1.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.ObjectStreamException;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {
    private FirebaseAuth mAuth;
    private CollectionReference usersRef;
    private CollectionReference chatsRef;
    private String currentUserID;

    private View mView;
    private RecyclerView recyclerViewChat;
    private LinearLayoutManager linearLayoutManager;
    private EditText editTextChatMessage;
    private ImageButton imageButtonSend;

    DateUtils dateUtils;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_chat, container, false);

        dateUtils = new DateUtils();

        mAuth = FirebaseAuth.getInstance();
        chatsRef = FirebaseFirestore.getInstance().collection("Chats");
        usersRef = FirebaseFirestore.getInstance().collection("Users");
        currentUserID = mAuth.getCurrentUser().getUid();

        linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recyclerViewChat = mView.findViewById(R.id.recyclerViewChat);
        recyclerViewChat.setLayoutManager(linearLayoutManager);

        editTextChatMessage = mView.findViewById(R.id.editTextChatMessage);
        imageButtonSend = mView.findViewById(R.id.imageButtonSend);

        imageButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = editTextChatMessage.getText().toString();
                sendMessage(message);
            }
        });

        editTextChatMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int len = charSequence.length();

                if (len == 0) {
                    imageButtonSend.setVisibility(View.GONE);
                } else {
                    imageButtonSend.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        loadChats();

        return mView;
    }

    public void loadChats() {
        Query query = chatsRef.orderBy("timestamp", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Chat> options = new FirestoreRecyclerOptions.Builder<Chat>().setQuery(query, Chat.class).build();
        FirestoreRecyclerAdapter<Chat, ChatHolder> firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<Chat, ChatHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChatHolder chatHolder, int i, @NonNull Chat chat) {
                chatHolder.setUser_id(chat.getUser_id(), chat);
            }

            @NonNull
            @Override
            public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_chat_layout, parent, false);
                return new ChatHolder(view);
            }
        };
        firestoreRecyclerAdapter.startListening();
        recyclerViewChat.setAdapter(firestoreRecyclerAdapter);

        recyclerViewChat.scrollToPosition(recyclerViewChat.getAdapter().getItemCount() - 1);
    }

    public void sendMessage(final String message) {
        usersRef.document(currentUserID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String date = dateUtils.getCurrentDate();
                    String time = dateUtils.getCurrentTime();
                    String initials = documentSnapshot.get("name_initials").toString();

                    Map<String, Object> map = new ArrayMap<>();
                    map.put("user_id", currentUserID);
                    map.put("initials", initials);
                    map.put("message", message);
                    map.put("date", date);
                    map.put("time", time);
                    map.put("timestamp", dateUtils.getCurrentTimestamp());


                    chatsRef.document().set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("Avery", "Chat sent");
                                editTextChatMessage.setText(null);
                            }
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Avery", e.getMessage());
            }
        });
    }

}
