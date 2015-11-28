package com.skku.se;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.navercorp.volleyextensions.volleyer.Volleyer;
import com.skku.se.JacksonClass.SectionContent;
import com.skku.se.JacksonClass.SyllabusInfo;

import org.json.JSONArray;

public class LearningActivity extends AppCompatActivity implements AlertDialogFragment.AlertDialogFragmentCallback {
	private static final String TAG = "LearningActivity";

	private ProgressBar mProgressBar;
	private Toolbar mToolbar;
	private NoSwipeViewpager mLearningFragmentViewPager;
	private LearningFragmentPagerAdapter mLearningFragmentPagerAdapter;

	AlertDialogFragment mAlertDialogFragment;

	private Button mPreviousButton;
	private Button mNextButton;

	private int mCurrentPageNumber = 0;

	private int mSectionId;
	private int mChapterId;
	private int mLearningLevel;

	private int mTotalSectionCount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_learning);

		Intent intent = getIntent();
		if (intent != null) {
			mChapterId = intent.getIntExtra(MainActivity.CHAPTER_ID, 1);
			mSectionId = intent.getIntExtra(MainActivity.SECTION_ID, 1);
			mLearningLevel = intent.getIntExtra(MainActivity.LEARNING_LEVEL, 1);
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
		}

		initProgressBar();
		downloadCurrentSectionLearningContents();
		downloadSectionList();
	}

	private void initProgressBar() {
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar_learning);
		mProgressBar.setVisibility(View.GONE);
	}

	private void startProgressBar() {
		mProgressBar.setVisibility(View.VISIBLE);
	}

	private void stopProgressBar() {
		mProgressBar.setVisibility(View.GONE);
	}

	private void downloadCurrentSectionLearningContents() {
		Volleyer.volleyer()
				.get(getResources().getString(R.string.root_url) + "chapters/" + mChapterId + "/levels/" + mLearningLevel + "/sections/" + mSectionId)
				.withTargetClass(SectionContent.class).withListener(new Response.Listener<SectionContent>() {
			@Override
			public void onResponse(SectionContent response) {
				stopProgressBar();
				configureToolbar(response.sectionTitle, response.chapterTitle);
				configureFragmentViewPager(response);
				configurePageControlButtons();
			}
		}).withErrorListener(new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				stopProgressBar();
				Toast.makeText(LearningActivity.this, "학습 콘텐츠를 가져오지 못했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
			}
		}).execute();

		startProgressBar();
	}

	private void downloadSectionList() {
		Volleyer.volleyer().get(getResources().getString(R.string.root_url) +
				"chapters/" +
				mChapterId +
				"/levels/" +
				mLearningLevel +
				"/sections").withTargetClass(SyllabusInfo.class).withListener(new Response.Listener<SyllabusInfo>() {
			@Override
			public void onResponse(SyllabusInfo response) {
				mTotalSectionCount = response.sections.size();
			}
		}).execute();
	}

	private void uploadCurrentLearningSection() {
		//TODO upload current learning section data to the server
	}

	@Override
	public void onBackPressed() {
		showStopLearningDialog();
	}

	private void showStopLearningDialog() {
		mAlertDialogFragment = null;
		mAlertDialogFragment = new AlertDialogFragment();
		mAlertDialogFragment.setMessage(R.string.are_you_sure_you_want_to_stop_learning);
		mAlertDialogFragment.setNumberOfButtons(2);
		mAlertDialogFragment.setNegativeButtonText(R.string.no);
		mAlertDialogFragment.setPositiveButtonText(R.string.yes);
		mAlertDialogFragment.show(getSupportFragmentManager(), "stopLearningDialog");
	}

	private void showCompleteLearningDialog() {
		mAlertDialogFragment = null;
		mAlertDialogFragment = new AlertDialogFragment();
		mAlertDialogFragment.setMessage(R.string.you_completed_this_chapter);
		mAlertDialogFragment.setNumberOfButtons(1);
		mAlertDialogFragment.setPositiveButtonText(R.string.confirm);
		mAlertDialogFragment.show(getSupportFragmentManager(), "completedLearningDialog");
	}

	@Override
	public void onClickPositiveButton() {
		uploadCurrentLearningSection();
		Toast.makeText(LearningActivity.this, "학습 진행 정보가 저장되었습니다.", Toast.LENGTH_SHORT).show();
		finish();
	}

	@Override
	public void onClickNegativeButton() {
		mAlertDialogFragment.dismiss();
	}

	@Override
	public void onClickSelectionButton(int index) {}

	private void configureToolbar(String sectionTitle, String chapterTitle) {
		mToolbar = (Toolbar) findViewById(R.id.toolbar_learning);
		mToolbar.setTitle(sectionTitle);
		mToolbar.setSubtitle(chapterTitle);
		mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
		mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showStopLearningDialog();
			}
		});
	}

	private void refresh() {
		mLearningFragmentViewPager = null;
		mLearningFragmentPagerAdapter = null;

		mPreviousButton = null;
		mNextButton = null;

		mCurrentPageNumber = 0;
	}

	private void showNextSection() {
		mSectionId += 1;
		downloadCurrentSectionLearningContents();
	}

	private void configureFragmentViewPager(SectionContent sectionContent) {
		mLearningFragmentViewPager = (NoSwipeViewpager) findViewById(R.id.viewPager_learning);
		mLearningFragmentViewPager.setPagingEnabled(false);
		mLearningFragmentPagerAdapter = new LearningFragmentPagerAdapter(getSupportFragmentManager(), sectionContent);

		mLearningFragmentViewPager.setAdapter(mLearningFragmentPagerAdapter);

		mLearningFragmentViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				mCurrentPageNumber = position;

				mNextButton.setOnClickListener(null);

				switch (position) {
					case 0:
						mPreviousButton.setVisibility(View.INVISIBLE);
						mNextButton.setOnClickListener(mNextPageNavigationListener);
						break;
					case 1:
						mPreviousButton.setVisibility(View.VISIBLE);
						mNextButton.setOnClickListener(mNextPageNavigationListener);
						break;
					case 2:
						mPreviousButton.setVisibility(View.VISIBLE);
						if (mSectionId < mTotalSectionCount) {
							mNextButton.setText(R.string.next_section);
							mNextButton.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									refresh();
									showNextSection();
								}
							});
						} else {
							mNextButton.setText(R.string.finish_learning);
							mNextButton.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									showCompleteLearningDialog();
								}
							});
						}
						break;
				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
	}

	private void configurePageControlButtons() {
		mPreviousButton = (Button) findViewById(R.id.button_previous_page);
		mPreviousButton.setText(R.string.previous_page);
		mPreviousButton.setVisibility(View.INVISIBLE);
		mPreviousButton.setOnClickListener(mPreviousPageNavigationListener);

		mNextButton = (Button) findViewById(R.id.button_next_page);
		mNextButton.setText(R.string.next_page);
		mNextButton.setOnClickListener(mNextPageNavigationListener);
	}

	private View.OnClickListener mPreviousPageNavigationListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mCurrentPageNumber > 0) {
				mLearningFragmentViewPager.setCurrentItem(--mCurrentPageNumber);
			}
		}
	};

	private View.OnClickListener mNextPageNavigationListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mCurrentPageNumber < 3) {
				mLearningFragmentViewPager.setCurrentItem(++mCurrentPageNumber);
			}
		}
	};

	public static class LearningFragmentPagerAdapter extends FragmentStatePagerAdapter {
		private SectionContent mSectionContent;

		public LearningFragmentPagerAdapter(FragmentManager fragmentManager, SectionContent sectionContent) {
			super(fragmentManager);
			mSectionContent = sectionContent;
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public Fragment getItem(int position) {
			Bundle bundle = new Bundle();
			bundle.putInt("chapterId", mSectionContent.chapterId);
			bundle.putInt("level", mSectionContent.level);
			bundle.putInt("sectionId", mSectionContent.sectionId);
			return LearningFragment.newInstance(position + 1, bundle);
		}
	}
}
