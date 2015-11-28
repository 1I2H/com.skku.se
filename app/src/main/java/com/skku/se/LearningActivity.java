package com.skku.se;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.navercorp.volleyextensions.volleyer.Volleyer;
import com.skku.se.JacksonClass.SectionContent;
import com.skku.se.JacksonClass.SyllabusInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LearningActivity extends AppCompatActivity {
	private static final String TAG = "LearningActivity";

	private Toolbar mToolbar;
	private NoSwipeViewpager mLearningFragmentViewPager;
	private LearningFragmentPagerAdapter mLearningFragmentPagerAdapter;

	private Button mPreviousButton;
	private Button mNextButton;

	private int mCurrentPageNumber = 0;
	private JSONArray mLearningContentsList;

	private int mSectionId;
	private int mChapterId;
	private int mLearningLevel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_learning);

		// init mock server
		MockServer.getInstance(this).makeLearningContentsList();
		mLearningContentsList = MockServer.getInstance(this).getLearningContentsList();

		Intent intent = getIntent();
		if (intent != null) {
			mChapterId = intent.getIntExtra(MainActivity.CHAPTER_ID, 1);
			mSectionId = intent.getIntExtra(MainActivity.SECTION_ID, 1);
			mLearningLevel = intent.getIntExtra(MainActivity.LEARNING_LEVEL, 1);
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
		}

		configurePageControlButtons();
		downloadCurrentSectionLearningContents();
		downloadSectionList();
	}

	private void downloadCurrentSectionLearningContents() {
		Volleyer.volleyer()
				.get(getResources().getString(R.string.root_url) + "chapters/" + mChapterId + "/levels/" + mLearningLevel + "/sections/" + mSectionId)
				.withTargetClass(SectionContent.class).withListener(new Response.Listener<SectionContent>() {
			@Override
			public void onResponse(SectionContent response) {
				configureToolbar(response.sectionTitle, response.chapterTitle);
				configureFragmentViewPager(response);
			}
		}).withErrorListener(new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		}).execute();
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

			}
		}).withErrorListener(new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		}).execute();
	}

	private void uploadCurrentLearningSection() {

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	private void configureToolbar(String sectionTitle, String chapterTitle) {
		mToolbar = (Toolbar) findViewById(R.id.toolbar_learning);
		mToolbar.setTitle(sectionTitle);
		mToolbar.setSubtitle(chapterTitle);
		mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
		mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
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
				if (position == 0) {
					mPreviousButton.setVisibility(View.INVISIBLE);
				} else {
					mPreviousButton.setVisibility(View.VISIBLE);
				}

				switch (position) {
					case 0:
						mPreviousButton.setVisibility(View.INVISIBLE);
						break;
					case 1:

						break;
					case 3:
						mNextButton.setText(R.string.next_section);
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
		mPreviousButton.setVisibility(View.INVISIBLE);
		mPreviousButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mCurrentPageNumber > 0) {
					mLearningFragmentViewPager.setCurrentItem(--mCurrentPageNumber);
				}
			}
		});

		mNextButton = (Button) findViewById(R.id.button_next_page);
		mNextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mCurrentPageNumber < mLearningContentsList.length()) {
					mLearningFragmentViewPager.setCurrentItem(++mCurrentPageNumber);
				}
			}
		});
	}

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
