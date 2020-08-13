package com.sample.android.contact.domain;

import androidx.fragment.app.Fragment;

public class MainPagerItem {

    private Fragment fragment;
    private String title;

    public MainPagerItem(Fragment fragment, String title) {
        this.fragment = fragment;
        this.title = title;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public String getTitle() {
        return title;
    }
}
