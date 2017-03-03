package com.evo.passwordgenerator.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.internal.ThemeSingleton;
import com.evo.passwordgenerator.R;
import com.evo.passwordgenerator.data.PasswordContract;
import com.evo.passwordgenerator.dialogs.ChangelogDialog;
import com.evo.passwordgenerator.fragments.GenerateFragment;
import com.evo.passwordgenerator.items.SaveItem;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private static final String PRIVATE_PREF = "myapp";
    private static final String VERSION_KEY = "version_number";
    private int[] tabIcons = {
            R.drawable.tab_alpha_selector,
            R.drawable.tab_num_selector,
            R.drawable.tab_alphanumsym_selector
    };

    public static final int LOADER_ID = 100;

    public static final String[] SAVE_COLUMNS = {
            PasswordContract.PasswordEntry._ID,
            PasswordContract.PasswordEntry.COLUMN_PASSWORD,
    };
    public static final int COL_ID = 0;
    public static final int COL_PASSWORD = 1;

    FastItemAdapter fastItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateRecentTasksUi();
        setContentView(R.layout.main_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        init();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.save_recyclerview);

        fastItemAdapter = new FastItemAdapter();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(fastItemAdapter);

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = PasswordContract.PasswordEntry.CONTENT_URI;

        return new CursorLoader(this,
                uri,
                SAVE_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        if (c != null) {
            fastItemAdapter.clear();
            for (int i = (c.getCount()) - 1; i >= 0; i--) {
                c.moveToPosition(i);
                fastItemAdapter.add(new SaveItem(c.getLong(COL_ID), c.getString(COL_PASSWORD)));
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(GenerateFragment.newInstance(0));
        adapter.addFragment(GenerateFragment.newInstance(1));
        adapter.addFragment(GenerateFragment.newInstance(2));
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }

    private void init() {
        SharedPreferences sharedPref = getSharedPreferences(PRIVATE_PREF, Context.MODE_PRIVATE);
        int currentVersionNumber = 0;
        int savedVersionNumber = sharedPref.getInt(VERSION_KEY, 0);

        try {
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            currentVersionNumber = pi.versionCode;
        } catch (Exception e) {
        }

        if (currentVersionNumber > savedVersionNumber) {
            showChangelog();

            SharedPreferences.Editor editor = sharedPref.edit();

            editor.putInt(VERSION_KEY, currentVersionNumber);
            editor.commit();
        }
    }


    public void showChangelog() {
        int accentColor = ThemeSingleton.get().widgetColor;
        if (accentColor == 0)
            accentColor = ContextCompat.getColor(this, R.color.colorAccent);
        ChangelogDialog.create(false, accentColor)
                .show(getSupportFragmentManager(), "changelog");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                Intent about = new Intent(this, AboutActivity_test2.class);
                startActivity(about);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateRecentTasksUi() {
        final ActivityManager.TaskDescription taskDescription =
                new ActivityManager.TaskDescription(
                        getString(R.string.app_name),
                        null,
                        ContextCompat.getColor(this, R.color.colorAccent));

        setTaskDescription(taskDescription);
    }
}
