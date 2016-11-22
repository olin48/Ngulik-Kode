package com.panelic.ngulikode;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.widget.Toast;

import com.panelic.ngulikode.rss.ServiceStarter;
import com.panelic.ngulikode.util.Helper;
import com.panelic.ngulikode.util.ScrimInsetsFrameLayout;
import com.panelic.ngulikode.web.WebviewFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity implements
		NavDrawerCallback {

	private Toolbar mToolbar;
	private NavDrawerFragment mNavigationDrawerFragment;

	public static String DATA = "transaction_data";
	public static boolean TABLET_LAYOUT = true;

    //Permissions Qeueu
	NavItem queueItem;
	Fragment queueFragment;

	SharedPreferences prefs;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		boolean newDrawer = getResources().getBoolean(R.bool.newdrawer);

		if (useTabletMenu()){
			setContentView(R.layout.activity_main_tablet);
			Helper.setStatusBarColor(MainActivity.this,
					ContextCompat.getColor(this, R.color.myPrimaryDarkColor));
		} else if (newDrawer == true) {
			setContentView(R.layout.activity_main_alternate);
		} else {
			setContentView(R.layout.activity_main);
			Helper.setStatusBarColor(MainActivity.this,
					ContextCompat.getColor(this, R.color.myPrimaryDarkColor));
		}

		mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
		setSupportActionBar(mToolbar);

		if (!useTabletMenu())
			getSupportActionBar().setDisplayShowHomeEnabled(true);
		else {
			getSupportActionBar().setDisplayShowHomeEnabled(false);
		}

		mNavigationDrawerFragment = (NavDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_drawer);

		if (newDrawer == true && !useTabletMenu()) {
			mNavigationDrawerFragment.setup(R.id.scrimInsetsFrameLayout,
					(DrawerLayout) findViewById(R.id.drawer), mToolbar);
			mNavigationDrawerFragment
					.getDrawerLayout()
					.setStatusBarBackgroundColor(
							ContextCompat.getColor(this, R.color.myPrimaryDarkColor));

	        ((ScrimInsetsFrameLayout) findViewById(R.id.scrimInsetsFrameLayout)).getLayoutParams().width = getDrawerWidth();
		} else {
			mNavigationDrawerFragment.setup(R.id.fragment_drawer,
					(DrawerLayout) findViewById(R.id.drawer), mToolbar);
			 
			DrawerLayout.LayoutParams params = (android.support.v4.widget.DrawerLayout.LayoutParams) mNavigationDrawerFragment.getView().getLayoutParams();
		    params.width = getDrawerWidth();
		    mNavigationDrawerFragment.getView().setLayoutParams(params);
		}

		if (useTabletMenu()) {
			mNavigationDrawerFragment
					.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
			mNavigationDrawerFragment
					.getDrawerLayout().setScrimColor(Color.TRANSPARENT);
		} else {
			mNavigationDrawerFragment
						.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
		}

		Helper.admobLoader(this, getResources(), findViewById(R.id.adView));

		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

		// setting push enabled
		String push = getString(R.string.rss_push_url);
		if (null != push && !push.equals("")) {
			// Create object of SharedPreferences.
			boolean firstStart = prefs.getBoolean("firstStart", true);

			if (firstStart) {

				final ServiceStarter alarm = new ServiceStarter();

				SharedPreferences.Editor editor = prefs.edit();

				alarm.setAlarm(this);
				// now, just to be sure, where going to set a value to check if
				// notifications is really enabled
				editor.putBoolean("firstStart", false);
				// commits your edits
				editor.commit();
			}

		}

		// Checking if the user would prefer to show the menu on start
		boolean checkBox = prefs.getBoolean("menuOpenOnStart", false);
		if (checkBox == true && !useTabletMenu()) {
			mNavigationDrawerFragment.openDrawer();
		}

		// New imageloader
		Helper.initializeImageLoader(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.rss_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@SuppressLint("NewApi")
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case 1:
				boolean foundfalse = false;
				for (int i = 0; i < grantResults.length; i++) {
					if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
						foundfalse = true;
					}
				}
				if (!foundfalse){
					openFragment(queueItem, queueFragment);
				} else {
					// Permission Denied
					Toast.makeText(MainActivity.this, getResources().getString(R.string.permissions_required), Toast.LENGTH_SHORT)
							.show();
				}
				break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

	void openFragment(NavItem item, Fragment fragment){
				Bundle bundle = new Bundle();
				String extra;

				String license = getResources().getString(R.string.google_play_license);
				// if item does not require purchase, or app has purchased, or license is null/empty (app has no in app purchases)
				if (item.requiresPurchase() == true
						&& !SettingsFragment.getIsPurchased(this)
						&& null != license && !license.equals("")) {
					fragment = new SettingsFragment();
					extra = SettingsFragment.SHOW_DIALOG;
				} else {
					extra = item.getData();
				}

				bundle.putString(DATA, extra);
				fragment.setArguments(bundle);

				FragmentManager fragmentManager = getSupportFragmentManager();

				fragmentManager.beginTransaction()
						.replace(R.id.container, fragment).commitAllowingStateLoss();

				if(!useTabletMenu())
					setTitle(item.getText());

				if (null != MainActivity.this.getSupportActionBar()
						&& null != MainActivity.this.getSupportActionBar()
						.getCustomView()) {

				}
	}

	@SuppressLint("NewApi")
	@Override
	public void onNavigationDrawerItemSelected(int position, NavItem item) {
		Fragment fragment;
		try {
			fragment = item.getFragment().newInstance();
			if (fragment != null) {
				//TODO An explaination before asking

				openFragment(item, fragment);
			} else {
				Log.v("INFO", "Error creating fragment");
			}

		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onBackPressed() {
		Fragment webview = getSupportFragmentManager().findFragmentById(
				R.id.container);

		if (mNavigationDrawerFragment.isDrawerOpen()) {
			mNavigationDrawerFragment.closeDrawer();
		} else if (webview instanceof WebviewFragment) {
			boolean goback = ((WebviewFragment) webview).canGoBack();
			if (!goback)
				super.onBackPressed();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		List<Fragment> fragments = getSupportFragmentManager().getFragments();
		if (fragments != null) {
			for (Fragment frag : fragments)
				frag.onActivityResult(requestCode, resultCode, data);
		}
	}
	
	private int getDrawerWidth(){
		// Navigation Drawer layout width
		int width = getResources().getDisplayMetrics().widthPixels;
		
		TypedValue tv = new TypedValue();
		int actionBarHeight;
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        } else {
        	actionBarHeight = 0;
        }
        
        int possibleMinDrawerWidth = width - actionBarHeight;
        
        int maxDrawerWidth = getResources().getDimensionPixelSize(R.dimen.drawer_width);
        
        return Math.min(possibleMinDrawerWidth, maxDrawerWidth);
	}

	public boolean useTabletMenu(){
		return (getResources().getBoolean(R.bool.isWideTablet) && TABLET_LAYOUT);
	}
}