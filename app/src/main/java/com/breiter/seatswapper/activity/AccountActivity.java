package com.breiter.seatswapper.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.breiter.seatswapper.R;
import com.breiter.seatswapper.adapter.ViewPagerAdapter;
import com.breiter.seatswapper.model.Message;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountActivity extends AppCompatActivity {

    private DatabaseReference reference;
    private FirebaseUser currentUser;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private TextView messageAlertTextView;
    private RelativeLayout alertLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        tabLayout = findViewById(R.id.tabLayout);

        viewPager = findViewById(R.id.viewPager);

        setupFragmentPagerAdapter(); //1

    }



    //1
    private void setupFragmentPagerAdapter() {

        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        setupTabIcons();   //2

        addMessageAlert(); //3

    }



    // 2. Setup tab icons - blue for active tab and grey for unselected tabs
    private void setupTabIcons() {

        final int[] tabIcons = {
                R.drawable.ic_chat,
                R.drawable.ic_flight,
                R.drawable.ic_profile,
                R.drawable.ic_chat_ia,
                R.drawable.ic_flight_ia,
                R.drawable.ic_profile_ia,
        };

        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[4]);
        tabLayout.getTabAt(2).setIcon(tabIcons[5]);


        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {

                    case 0:
                        tab.setIcon(tabIcons[0]);
                        tabLayout.getTabAt(1).setIcon(tabIcons[4]);
                        tabLayout.getTabAt(2).setIcon(tabIcons[5]);
                        break;

                    case 1:
                        tab.setIcon(tabIcons[1]);
                        tabLayout.getTabAt(0).setIcon(tabIcons[3]);
                        tabLayout.getTabAt(2).setIcon(tabIcons[5]);
                        break;

                    case 2:
                        tab.setIcon(tabIcons[2]);
                        tabLayout.getTabAt(0).setIcon(tabIcons[3]);
                        tabLayout.getTabAt(1).setIcon(tabIcons[4]);
                        break;

                }


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }



    //3
    private void addMessageAlert() {

        alertLayout = findViewById(R.id.alertLayout);

        messageAlertTextView = findViewById(R.id.messageAlertTextView);

        reference = FirebaseDatabase.getInstance().getReference("Messages").child(currentUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int countUnread = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    if (snapshot.exists()) {
                        Message message = snapshot.getValue(Message.class);

                        if (message != null) {

                            if (message.getType().equals("pending request")) {

                                if (!message.getRequester().equals(currentUser.getUid()) && !message.isIsread())
                                    countUnread++;

                            } else if (!message.getResponder().equals(currentUser.getUid()) && !message.isIsread())
                                countUnread++;

                        }

                        if (countUnread == 0)
                            alertLayout.setVisibility(View.GONE);

                        else {
                            alertLayout.setVisibility(View.VISIBLE);
                            messageAlertTextView.setText(Integer.toString(countUnread));

                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


}

