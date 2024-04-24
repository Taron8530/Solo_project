package com.example.solo_project;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
public class history_content_adapter extends FragmentStateAdapter {
    private final ArrayList<Fragment> mFragmentList = new ArrayList<>();
    public history_content_adapter(@NonNull  FragmentManager  FragmentManager , @NonNull Lifecycle lifecycle) {
        super(FragmentManager, lifecycle);

    }

    public void addFragment(Fragment fragment){

        mFragmentList.add(fragment);

    }

    @NonNull

    @Override

    public Fragment createFragment(int position) {

        return mFragmentList.get(position);

    }

    @Override

    public int getItemCount() {

        return mFragmentList.size();

    }
}
