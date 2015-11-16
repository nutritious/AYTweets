package com.codepath.apps.aytweets.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.apps.aytweets.fragments.TimelineFragment;
import com.codepath.apps.aytweets.fragments.TimelineFragmentBase;
import com.codepath.apps.aytweets.network.TimelineType;

import java.util.HashMap;

/**
 * Created by ayegorov on 11/16/15.
 */
public class TimelinePagerAdapter extends FragmentPagerAdapter {
    private String tabTitles[] = {"Home", "Mentions"};
    private HashMap<Integer, TimelineFragment> timelineFragments;

    public TimelinePagerAdapter(FragmentManager manager) {
        super(manager);

        timelineFragments = new HashMap<>();
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public Fragment getItem(int position) {
        TimelineFragment fragment = timelineFragments.get(position);
        if (fragment == null) {
            fragment = TimelineFragment.newInstance(TimelineType.values()[position + 1]);
            timelineFragments.put(position, fragment);
        }

        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    public TimelineFragmentBase getItemByTimelineType(TimelineType timelineType) {
        if (timelineType != TimelineType.Undefined) {
            return timelineFragments.get(timelineType.ordinal() - 1);
        } else {
            return null;
        }
    }
}
