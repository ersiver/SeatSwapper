package com.breiter.seatswapper.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.breiter.seatswapper.R;
import com.breiter.seatswapper.adapter.MailAdapter;
import com.breiter.seatswapper.model.Message;
import com.breiter.seatswapper.tool.SwipeController;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MailFragment extends Fragment {

    private DatabaseReference reference;
    private FirebaseUser currentUser;
    private RecyclerView mailRecyclerView;
    private List<Message> mailList;
    private MailAdapter mailAdapter;
    private SwipeController swipeController;
    private ItemTouchHelper itemTouchhelper;


    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_mail, container, false);
        mailList = new ArrayList<>();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mailAdapter = new MailAdapter(requireContext(), mailList);
        initRecyclerView(view); //1
        getUserMailList();      //2
        return view;
    }

    //1.
    private void initRecyclerView(View view) {

        mailRecyclerView = view.findViewById(R.id.mailRecyclerView);
        mailRecyclerView.setHasFixedSize(true);
        mailRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        swipeController = new SwipeController(requireContext(), mailAdapter, mailList);
        itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(mailRecyclerView);

    }


    //2. Retrieve current user mails and add to the list:
    private void getUserMailList() {

        reference = FirebaseDatabase.getInstance().getReference("Messages").child(currentUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mailList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    assert message != null;
                    if(!message.isHide())
                    mailList.add(message);
                }

                //Sort the mail list according to the time and display
                Collections.sort(mailList);
                mailRecyclerView.setAdapter(mailAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


}


