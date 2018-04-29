package com.example.jennifer.contracker;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by kezhao on 4/6/18.
 */

class TabsPagerAdapter extends FragmentPagerAdapter {
    
    //implement the bottom tab
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    //user can select different tab bar menu options 
    @Override
    public Fragment getItem(int position) {
        switch (position){
            //go to the job page
            case 0:
                JobsFragment jobsFragment = new JobsFragment();
                return jobsFragment;
            
            //go to the chat page
            case 1:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;

            //go to the profile page
            case 2:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

//    public CharSequence getPageTitle(int position){
//        switch (position){
//            case 0:
//                return "Jobs";
//            case 1:
//                return "Chats";
//            case 2:
//                return "Friends";
//            default:
//                return null;
//        }
//    }
}
