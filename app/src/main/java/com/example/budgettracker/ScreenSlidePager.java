package com.example.budgettracker;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;

public class ScreenSlidePager extends Fragment {
    private static final int NUM_PAGES = 5;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.screen_slide_pager, container, false);
        ViewPager mPager = view.findViewById(R.id.pager);
        PagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        mPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(mPager);

        return view;
    }


    private static class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        private final String[] pageTypes = {"income", "expenses", "monthlyExpense", "personalExpense", "personalIncome"};
        private final String[] pageTitles = {"Income", "Expenses", "Monthly\nExpenses", "Personal\nExpenses", "Personal\nIncomes"};

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return ShowDataPageFragment.newInstance(pageTypes[position]);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return pageTitles[position];
        }
    }

}