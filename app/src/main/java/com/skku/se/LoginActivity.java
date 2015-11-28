package com.skku.se;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

public class LoginActivity extends AppCompatActivity implements SignInFragment.SignInFragmentCallback, SignUpFragment.SignUpFragmentCallback {
	private static final String TAG = "LoginActivity";

	private NoSwipeViewpager mNoSwipeViewpager;
	private LoginFragmentPagerAdapter mLoginFragmentPagerAdapter;

	private ProgressBar mProgressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		mNoSwipeViewpager = (NoSwipeViewpager) findViewById(R.id.viewPager_login);
		mLoginFragmentPagerAdapter = new LoginFragmentPagerAdapter(getSupportFragmentManager());
		mNoSwipeViewpager.setAdapter(mLoginFragmentPagerAdapter);
		mNoSwipeViewpager.setPagingEnabled(false);

		initProgressBar();
	}

	private void initProgressBar() {
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar_login);
		mProgressBar.setVisibility(View.GONE);
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
	public void onClickNavigationBackButton() {
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

	@Override
	public void startProgressBar() {
		mProgressBar.setVisibility(View.VISIBLE);
	}

	@Override
	public void stopProgressBar() {
		mProgressBar.setVisibility(View.GONE);
	}
}
