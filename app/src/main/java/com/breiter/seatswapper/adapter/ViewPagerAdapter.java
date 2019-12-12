package com.breiter.seatswapper.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.breiter.seatswapper.fragment.FlightFragment;
import com.breiter.seatswapper.fragment.MailFragment;
import com.breiter.seatswapper.fragment.ProfileFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private static final int NUM_PAGES = 3;

    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        if (position == 0)
            return new MailFragment();

        else if (position == 1)
            return new FlightFragment();

        else
            return new ProfileFragment();

    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        if (position == 0)
            return "Mail";

        else if (position == 1)
            return "Flights";

        else
            return "Profile";

    }


}








