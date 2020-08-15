package com.example.csci571hw9;


import android.content.Context;


import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


import com.example.csci571hw9.BusinessFragment;
import com.example.csci571hw9.R;
import com.example.csci571hw9.WorldFragment;

public class HeadlinesFragmentPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{
            R.string.world,
            R.string.business,
            R.string.politics,
            R.string.sports,
            R.string.technology,
            R.string.science};
    private final Context mContext;

    public HeadlinesFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        if(position == 0){
            return new WorldFragment();
        }
        else if(position == 1){
            return new BusinessFragment();
        }
        else if(position == 2){
            return new PoliticsFragment();
        }
        else if(position == 3){
            return new SportsFragment();
        }
        else if(position == 4){
            return new TechnologyFragment();
        }
        else if(position == 5){
            return new ScienceFragment();
        }
        else{
            return new BusinessFragment();
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 6;
    }
}