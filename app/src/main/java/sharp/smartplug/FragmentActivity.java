package sharp.smartplug;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.vision.text.Text;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.blnetwork.BLNetwork;

import static sharp.smartplug.R.styleable.ActionBar;

public class FragmentActivity extends AppCompatActivity implements FragmentTimer.OnFragmentInteractionListener{

    TabLayout tabLayout;
    ViewPager viewPager;
    private String TAG = "FragmentActivity";
    int count,position,frag_posn;
    ArrayList<String> mac_addr = new ArrayList<String>();
    ArrayList<String> dev_name = new ArrayList<String>();

    private int[] tabIcons = {
            R.drawable.mini_control,
            R.drawable.mini_time
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Bundle extras = getIntent().getExtras();
        frag_posn = extras.getInt("OpenFragmentTimer");

        if (extras != null) {
            mac_addr = extras.getStringArrayList("mac");
            dev_name = extras.getStringArrayList("name");
            count = extras.getInt("count");
            position = extras.getInt("position");
        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }

    @Override
    public void onResume() {
        super.onResume();
        NetworkUtil networkUtil = new NetworkUtil(FragmentActivity.this);
        networkUtil.startScan();
    }

    private void setupTabIcons() {

        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentOnOff(), "ON/OFF");
        adapter.addFragment(new FragmentTimer(), "Timer");
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(frag_posn);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

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

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
