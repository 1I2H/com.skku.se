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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
	private String mSectionName;
	private String mChapterName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_learning);

		// init mock server
		MockServer.getInstance(this).makeLearningContentsList();
		mLearningContentsList = MockServer.getInstance(this).getLearningContentsList();

		Intent intent = getIntent();
		if (intent != null) {
			mChapterName = intent.getStringExtra(MainActivity.CHAPTER_NAME);
			mSectionName = intent.getStringExtra(MainActivity.SECTION_NAME);
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
		}

		configureToolbar();
		configureFragmentViewPager();
		configurePageControlButtons();
	}

	private void configureToolbar() {
		mToolbar = (Toolbar) findViewById(R.id.toolbar_learning);
		mToolbar.setTitle(mSectionName);
		mToolbar.setSubtitle(mChapterName);
	}

	private void configureFragmentViewPager() {
		mLearningFragmentViewPager = (NoSwipeViewpager) findViewById(R.id.viewPager_learning);
		mLearningFragmentViewPager.setPagingEnabled(false);
		mLearningFragmentPagerAdapter = new LearningFragmentPagerAdapter(getSupportFragmentManager(), mLearningContentsList);

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
		private JSONArray mLearningContentsList;

		public LearningFragmentPagerAdapter(FragmentManager fragmentManager, JSONArray learningContentsList) {
			super(fragmentManager);
			mLearningContentsList = learningContentsList;
		}

		@Override
		public int getCount() {
			return mLearningContentsList.length();
		}

		@Override
		public Fragment getItem(int position) {
			JSONObject singleLearningContents = mLearningContentsList.optJSONObject(position);
			return LearningFragment.newInstance(singleLearningContents.optInt("pageType"), singleLearningContents.toString());
		}
	}

	public static class LearningFragment extends Fragment {
		private static final String PAGE_TYPE = "page_type";
		private static final String PAGE_LEARNING_CONTENTS = "page_contents";
		private static final int LEARNING_TEMPLATE_ONE = 1;
		private static final int LEARNING_TEMPLATE_TWO = 2;
		private static final int LEARNING_TEMPLATE_THREE = 3;

		public int mPageType;
		public JSONObject mPageLearningContents;

		/* Views for learning template one */
		private TextView mLearningContentsTextView;

		/**
		 * Create a new instance of CountingFragment, providing "num"
		 * as an argument.
		 */
		public static LearningFragment newInstance(int pageType, String pageLearningContents) {
			LearningFragment learningFragment = new LearningFragment();
			Bundle args = new Bundle();
			args.putInt(PAGE_TYPE, pageType);
			args.putString(PAGE_LEARNING_CONTENTS, pageLearningContents);
			learningFragment.setArguments(args);
			return learningFragment;
		}

		/**
		 * When creating, retrieve this instance's number from its arguments.
		 */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			mPageType = getArguments() != null ? getArguments().getInt(PAGE_TYPE) : 1;
			String pageLearningContents = getArguments() != null ? getArguments().getString(PAGE_LEARNING_CONTENTS) : "";

			try {
				mPageLearningContents = new JSONObject(pageLearningContents);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		/**
		 * The Fragment's UI is just a simple text view showing its
		 * instance number.
		 */
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View templateView;
			switch (mPageType) {
				case LEARNING_TEMPLATE_ONE:
					templateView = inflater.inflate(R.layout.fragment_learning_template_one, container, false);
					configureLearningTemplateOneView(templateView);
					break;
				case LEARNING_TEMPLATE_TWO:
					templateView = inflater.inflate(R.layout.fragment_learning_template_one, container, false);
					break;
				case LEARNING_TEMPLATE_THREE:
					templateView = inflater.inflate(R.layout.fragment_learning_template_one, container, false);
					break;
				default:
					templateView = inflater.inflate(R.layout.fragment_learning_template_one, container, false);
					break;
			}
			return templateView;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
		}

		private void configureLearningTemplateOneView(View templateView) {
			mLearningContentsTextView = (TextView) templateView.findViewById(R.id.textView_learning_contents);
			mLearningContentsTextView.setText(mPageLearningContents.optString("pageInfo") + mPageLearningContents.optInt("subSectionNumber"));
		}
	}
}
