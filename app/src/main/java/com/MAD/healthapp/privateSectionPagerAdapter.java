package com.MAD.healthapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class privateSectionPagerAdapter extends FragmentPagerAdapter {
    public privateSectionPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                FriendsFragment friendsFragment1=new FriendsFragment();
                return  friendsFragment1;
            case 1:
                FriendsFragment friendsFragment2=new FriendsFragment();
                return  friendsFragment2;
            case 2:
                FriendsFragment friendsFragment=new FriendsFragment();
                return  friendsFragment;
            default:
                return null;



        }

    }

    @Override
    public int getCount() {
        return 3;
    }
    public CharSequence getPageTitle(int position)
    {
        switch(position){
            case 0:

                return  "Requests";
            case 1:

                return  "Chats";
            case 2:

                return  "Friends";
            default:
                return null;



        }


    }
}
