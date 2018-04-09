package com.viliyantrbr.nfcemvpayer.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.nfc.NfcAdapter;
import android.nfc.cardemulation.CardEmulation;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.viliyantrbr.nfcemvpayer.R;
import com.viliyantrbr.nfcemvpayer.adapter.TabLayoutFragmentPagerAdapter;
import com.viliyantrbr.nfcemvpayer.fragment.PaycardsTabFragment;
import com.viliyantrbr.nfcemvpayer.fragment.PaymentsTabFragment;
import com.viliyantrbr.nfcemvpayer.service.PaymentService;
import com.viliyantrbr.nfcemvpayer.util.LogUtil;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG, "\"" + TAG + "\": Activity create");

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ArrayList<TabLayoutFragmentPagerAdapter.ITabLayoutFragmentPagerAdapter> arrayList = new ArrayList<>();
        arrayList.add(new PaycardsTabFragment());
        arrayList.add(new PaymentsTabFragment());

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new TabLayoutFragmentPagerAdapter(getSupportFragmentManager(), arrayList));

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        final int tabIconColor = ContextCompat.getColor(getApplicationContext(), R.color.tabTextColor);
        final int tabSelectedIconColor = ContextCompat.getColor(getApplicationContext(), R.color.tabSelectedTextColor);

        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(arrayList.get(0).getIcon());

        Objects.requireNonNull(Objects.requireNonNull(tabLayout.getTabAt(0)).getIcon()).setColorFilter(tabSelectedIconColor, PorterDuff.Mode.SRC_IN);

        for (int i = 1; i < arrayList.size(); i++) {
            Objects.requireNonNull(tabLayout.getTabAt(i)).setIcon(arrayList.get(i).getIcon());

            Objects.requireNonNull(Objects.requireNonNull(tabLayout.getTabAt(i)).getIcon()).setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);

                if (tab.getIcon() != null) tab.getIcon().setColorFilter(tabSelectedIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);

                if (tab.getIcon() != null) tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                super.onTabReselected(tab);
            }
        });

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION)) {
            ComponentName paymentServiceComponentName = new ComponentName(this, PaymentService.class);

            CardEmulation cardEmulation = CardEmulation.getInstance(NfcAdapter.getDefaultAdapter(this));
            if (cardEmulation.isDefaultServiceForCategory(paymentServiceComponentName, CardEmulation.CATEGORY_PAYMENT)) {
                LogUtil.i(TAG, "\"" + getString(R.string.app_name) + "\" is default payment application");
            } else {
                LogUtil.i(TAG, "\"" + getString(R.string.app_name) + "\" is not default payment application");

                Intent intent = new Intent();
                intent.setAction(CardEmulation.ACTION_CHANGE_DEFAULT);
                intent.putExtra(CardEmulation.EXTRA_SERVICE_COMPONENT, paymentServiceComponentName);
                intent.putExtra(CardEmulation.EXTRA_CATEGORY, CardEmulation.CATEGORY_PAYMENT);

                startActivity(intent);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.d(TAG, "\"" + TAG + "\": Activity start");

        // Runtime permission(s)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> runtimePermissions = new ArrayList<>();

            runtimePermissions.add(Manifest.permission.NFC);

            runtimePermissions.add(Manifest.permission.INTERNET);

            runtimePermissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
            runtimePermissions.add(Manifest.permission.CHANGE_NETWORK_STATE);

            runtimePermissions.add(Manifest.permission.ACCESS_WIFI_STATE);
            runtimePermissions.add(Manifest.permission.CHANGE_WIFI_STATE);

            if (!runtimePermissions.isEmpty()) {
                ArrayList<String> requestRuntimePermissions = new ArrayList<>();

                for (String aRuntimePermission : runtimePermissions) {
                    if (checkSelfPermission(aRuntimePermission) != PackageManager.PERMISSION_GRANTED) requestRuntimePermissions.add(aRuntimePermission);
                }

                if (!requestRuntimePermissions.isEmpty()) requestPermissions(requestRuntimePermissions.toArray(new String[requestRuntimePermissions.size()]), 1);
            }
        }
        // - Runtime permission(s)
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (menu != null) {
            getMenuInflater().inflate(R.menu.menu_main, menu);

            return true;
        }

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem != null) {
            switch (menuItem.getItemId()) {
                case R.id.action_settings:
                    return true;

                default:
                    return super.onOptionsItemSelected(menuItem);
            }
        }

        return false;
    }
}