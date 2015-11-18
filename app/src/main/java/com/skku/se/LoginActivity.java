package com.skku.se;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity implements SignInFragment.SignInFragmentCallback, SignUpFragment.SignUpFragmentCallback {
	private static final String TAG = "LoginActivity";

	private NoSwipeViewpager mNoSwipeViewpager;
	private LoginFragmentPagerAdapter mLoginFragmentPagerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		mNoSwipeViewpager = (NoSwipeViewpager) findViewById(R.id.viewPager_login);
		mLoginFragmentPagerAdapter = new LoginFragmentPagerAdapter(getSupportFragmentManager());
		mNoSwipeViewpager.setAdapter(mLoginFragmentPagerAdapter);
		mNoSwipeViewpager.setPagingEnabled(false);
	}

	@Override
	public void onBackPressed() {
		if (mNoSwipeViewpager.getCurrentItem() == 1) {
			mNoSwipeViewpager.setCurrentItem(0);
			return;
		}
		super.onBackPressed();
	}

	@Override
	public void onClickGoSignUpPageButton() {
		mNoSwipeViewpager.setCurrentItem(1);
	}

	@Override
	public void onBackButton() {
		mNoSwipeViewpager.setCurrentItem(0);
	}

	public static class LoginFragmentPagerAdapter extends FragmentStatePagerAdapter {

		public LoginFragmentPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public Fragment getItem(int position) {
			if (position == 0) {
				return SignInFragment.newInstance();
			} else {
				return SignUpFragment.newInstance();
			}
		}
	}
}
