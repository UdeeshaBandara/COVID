package com.MAD.healthapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;


import com.google.android.material.tabs.TabLayout;


public class MsgFragment extends Fragment {
    private ViewPager viewPager;
    private privateSectionPagerAdapter sectionPagerAdapter;
    private TabLayout tabLayout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.msg_to_friend,container,false);
        viewPager=(ViewPager)v.findViewById(R.id.main_tabPager);
        sectionPagerAdapter=new privateSectionPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(sectionPagerAdapter);
        tabLayout=v.findViewById(R.id.main_tabs);
        tabLayout.setupWithViewPager(viewPager);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
