package com.skku.se;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.github.lzyzsd.circleprogress.CircleProgress;
import com.navercorp.volleyextensions.volleyer.Volleyer;
import com.skku.se.JacksonClass.AbstractChapterInfo;
import com.skku.se.JacksonClass.MainPageContents;
import com.skku.se.JacksonClass.QnAInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SyllabusFragment.SyllabusFragmentCallback,
		AlertDialogFragment.AlertDialogFragmentCallback {

	public static final String USER_ID = "user_id";
	public static final String CHAPTER_ID = "chapter_id";
	public static final String SECTION_ID = "section_id";
	public static final String LEARNING_LEVEL = "learning_level";
	private static final String TAG = "MainActivity";
	public static final int LEARNING_REQUEST = 1;

	private LinearLayout mLearningChapterListLinearLayout;
	private LinearLayout mQnALinearLayout;

	private ProgressBar mProgressBar;

	private ArrayList<AbstractChapterInfo> mAbstractChapterInfo;
	private ArrayList<QnAInfo> mQnAInfo;

	private CircleProgress mTempCircleProgress;
	private TextView mTempChapterProgressTextView;
	private TextView mTempTextView;
	private Button mTempContinueChapterButton;
	private Button mTempSeeSyllabusButton;

	private String mRootUrl;
	private int mCurrentChapterIndex = 0;
	private Response.Listener<MainPageContents> downloadMainPageContentListener = new Response.Listener<MainPageContents>() {
		@Override
		public void onResponse(MainPageContents response) {
			stopProgressBar();
			mAbstractChapterInfo = response.learning_info;
			mQnAInfo = response.qna;

			configureLearningList();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		mRootUrl = getResources().getString(R.string.root_url);

		initProgressBar();
		downloadMainPageContent();
	}

	private void initProgressBar() {
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar_main);
		mProgressBar.setVisibility(View.GONE);
	}

	private void downloadMainPageContent() {
		startProgressBar();
		Volleyer.volleyer().get(mRootUrl).addHeader("Authorization", restoreSessionFromSharedPreferences()).withTargetClass(MainPageContents.class)
				.withListener(downloadMainPageContentListener).withErrorListener(new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				stopProgressBar();
				Toast.makeText(MainActivity.this, "정보 가져오기를 실패하였습니다.", Toast.LENGTH_SHORT).show();
			}
		}).execute();
	}

	private void startProgressBar() {
		mProgressBar.setVisibility(View.VISIBLE);
	}

	private String restoreSessionFromSharedPreferences() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		Log.d(TAG, String.valueOf(sharedPreferences.getInt(Preferences.SESSION_ID, -1)));
		return String.valueOf(sharedPreferences.getInt(Preferences.SESSION_ID, -1));
	}

	private void stopProgressBar() {
		mProgressBar.setVisibility(View.GONE);
	}

	@Override
	public void onBackPressed() {
		if (getSupportFragmentManager().getBackStackEntryCount() >= 1) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				getWindow().setStatusBarColor(getResources().getColor(android.R.color.transparent));
				super.onBackPressed();
			}
		} else {
			showApplicationTerminationDialog();
		}
	}

	private void showApplicationTerminationDialog() {
		AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
		alertDialogFragment.setDialogStyle(AlertDialogFragment.SIMPLE_STYLE);
		alertDialogFragment.setMessage(R.string.are_you_sure_you_want_to_finish_the_app);
		alertDialogFragment.setPositiveButtonText(R.string.yes);
		alertDialogFragment.setNegativeButtonText(R.string.no);
		alertDialogFragment.setNumberOfButtons(2);
		alertDialogFragment.show(getSupportFragmentManager(), "application_termination_dialog");
	}

	private void configureLearningList() {
		configureLearningChapterList();
		configureQnAList();
	}

	private void configureLearningChapterList() {
		mLearningChapterListLinearLayout = (LinearLayout) findViewById(R.id.linearLayout_learning_chapter_list);

		try {
			for (int i = 0; i < mAbstractChapterInfo.size(); i++) {
				View chapterRow = inflateChapterRow();
				configureChapterRow(chapterRow, mAbstractChapterInfo.get(i));
				mLearningChapterListLinearLayout.addView(chapterRow);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void configureQnAList() {
		/* Q&A 목록 설정 */
		mQnALinearLayout = (LinearLayout) findViewById(R.id.linearLayout_qna);

		for (int i = 0; i < mQnAInfo.size(); i++) {
			QnAInfo qnAContent = mQnAInfo.get(i);

			View questionRow = inflateQnARow(true);
			configureQnARow(questionRow, qnAContent.section_title, qnAContent.question, qnAContent.question_date, true);
			mQnALinearLayout.addView(questionRow);

			if (qnAContent.answer != null) {
				View answerRow;
				answerRow = inflateQnARow(false);
				configureQnARow(answerRow, qnAContent.section_title, qnAContent.answer, qnAContent.answer_date, false);
				mQnALinearLayout.addView(answerRow);
			}
		}
	}

	private View inflateChapterRow() {
		View chapterRow = LayoutInflater.from(this).inflate(R.layout.layout_chapter_row, mLearningChapterListLinearLayout, false);
		LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) chapterRow.getLayoutParams();
		int marginTopValue = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
		layoutParams.setMargins(0, marginTopValue, 0, 0);
		chapterRow.setLayoutParams(layoutParams);

		return chapterRow;
	}

	private void configureChapterRow(View chapterRow, AbstractChapterInfo abstractChapterInfo) throws JSONException {
		final CircleProgress circleProgress = (CircleProgress) chapterRow.findViewById(R.id.circle_progress_chapter);
		TextView chapterTitleTextView = (TextView) chapterRow.findViewById(R.id.textView_chapter_title);
		final TextView chapterProgressTextView = (TextView) chapterRow.findViewById(R.id.textView_chapter_progress);
		final Button continueChapterButton = (Button) chapterRow.findViewById(R.id.button_continue_chapter);
		final Button seeSyllabusChapterButton = (Button) chapterRow.findViewById(R.id.button_see_sections_chapter);
		final TextView learningLevelChapterTextView = (TextView) chapterRow.findViewById(R.id.textView_learning_level_chapter);
		ImageView configureLearningLevelChapterImageView = (ImageView) chapterRow.findViewById(R.id.imageView_configure_learning_level_chapter);

		final int chapterId = abstractChapterInfo.chapter_id;
		final int learningLevel = abstractChapterInfo.current_level;
		final int currentSection = abstractChapterInfo.progress_info.get(learningLevel - 1).current_section;
		final String chapterTitle = abstractChapterInfo.chapter_title;
		final int progress = abstractChapterInfo.progress_info.get(learningLevel - 1).progress_percentage;

		learningLevelChapterTextView.setText(configureLearningLevel(learningLevel));
		learningLevelChapterTextView.setBackgroundResource(configureLearningLevelIcon(learningLevel));

		chapterProgressTextView.setText(abstractChapterInfo.progress_info.get(learningLevel - 1).section_title);

		chapterTitleTextView.setText(chapterTitle);

		circleProgress.setProgress(progress);

		if (isNewStart(currentSection)) {
			continueChapterButton.setText(R.string.start_learning);
		}

		if (progress == 100) {
			continueChapterButton.setText(R.string.finish_learning);
			continueChapterButton.setEnabled(false);
		} else {
			continueChapterButton.setEnabled(true);
		}

		continueChapterButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showLearningContents(chapterId, currentSection, learningLevel);
			}
		});

		seeSyllabusChapterButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showSyllabus(chapterId, currentSection, learningLevel);
			}
		});

		configureLearningLevelChapterImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCurrentChapterIndex = chapterId - 1;
				mTempCircleProgress = circleProgress;
				mTempTextView = learningLevelChapterTextView;
				mTempChapterProgressTextView = chapterProgressTextView;
				mTempContinueChapterButton = continueChapterButton;
				mTempSeeSyllabusButton = seeSyllabusChapterButton;

				showLearningLevelSelectionDialog();
			}
		});
	}

	private View inflateQnARow(boolean isQuestion) {
		View qnARow = LayoutInflater.from(this).inflate(R.layout.layout_qna_row, mQnALinearLayout, false);
		LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) qnARow.getLayoutParams();
		if (isQuestion) {
			int marginTopValue = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
			layoutParams.setMargins(0, marginTopValue, 0, 0);
		}
		qnARow.setLayoutParams(layoutParams);

		return qnARow;
	}

	private void configureQnARow(View qnARow, String qnASectionName, String qnADetail, String qnADate, boolean isQuestion) {
		TextView qnAIconTextView = (TextView) qnARow.findViewById(R.id.textView_QnA_icon);
		TextView qnASectionNameTextView = (TextView) qnARow.findViewById(R.id.textView_section_name);
		TextView qnADetailTextView = (TextView) qnARow.findViewById(R.id.textView_qna_detail);
		TextView qnADateTextView = (TextView) qnARow.findViewById(R.id.textView_date);

		qnAIconTextView.setText(configureQnAIconText(isQuestion));
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			qnAIconTextView.setBackground(getResources().getDrawable(configureQnAIcon(isQuestion)));
			qnASectionNameTextView.setText(qnASectionName);
		} else {
			qnAIconTextView.setBackground(getDrawable(configureQnAIcon(isQuestion)));
			qnASectionNameTextView.setVisibility(View.GONE);
		}
		qnADetailTextView.setText(qnADetail);
		qnADateTextView.setText(qnADate);
	}

	private String configureLearningLevel(int level) {
		if (level == 1) {
			return "Beginner";
		} else {
			return "Intermediate";
		}
	}

	private int configureLearningLevelIcon(int level) {
		if (level == 1) {
			return R.drawable.background_beginner_level_icon;
		} else {
			return R.drawable.background_intermediate_level_icon;
		}
	}

	private boolean isNewStart(int sectionId) {
		if (sectionId == 1) {
			return true;
		} else {
			return false;
		}
	}

	private void showLearningContents(int chapterId, int sectionId, int learningLevel) {
		callLearningActivity(chapterId, sectionId, learningLevel);
	}

	private void showSyllabus(int chapterId, int currentSectionId, int learningLevel) {
		SyllabusFragment syllabusFragment = SyllabusFragment.newInstance(chapterId, currentSectionId, learningLevel);
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(android.R.id.content, syllabusFragment);
		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
	}

	private void showLearningLevelSelectionDialog() {
		AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
		alertDialogFragment.setDialogStyle(AlertDialogFragment.RADIO_BUTTON_STYLE);
		alertDialogFragment.setTitle(R.string.select_learning_level);
		alertDialogFragment.setSelectionStringArray(R.array.learning_level_list);
		alertDialogFragment.show(getSupportFragmentManager(), "learning_level_selection_dialog");
	}

	private String configureQnAIconText(boolean isQuestion) {
		if (isQuestion) {
			return "Q";
		} else {
			return "A";
		}
	}

	private int configureQnAIcon(boolean isQuestion) {
		if (isQuestion) {
			return R.drawable.background_question_icon;
		} else {
			return R.drawable.background_answer_icon;
		}
	}

	private void callLearningActivity(int chapterId, int sectionId, int learningLevel) {
		Intent intent = new Intent(MainActivity.this, LearningActivity.class);
		intent.putExtra(CHAPTER_ID, chapterId);
		intent.putExtra(SECTION_ID, sectionId);
		intent.putExtra(LEARNING_LEVEL, learningLevel);
		startActivityForResult(intent, LEARNING_REQUEST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == LEARNING_REQUEST) {
			if (resultCode == RESULT_OK) {
				mLearningChapterListLinearLayout.removeAllViews();
				mQnALinearLayout.removeAllViews();
				downloadMainPageContent();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_sign_out) {
			signOut();
			return true;
		}
		return false;
	}

	private void signOut() {
		clearSharedPreference();
		restartApplication();
	}

	private void clearSharedPreference() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		sharedPreferences.edit().clear().apply();
	}

	private void restartApplication() {
		Intent intent = new Intent(MainActivity.this, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		finish();
	}

	@Override
	public void onClickNavigationBackButton() {
		getSupportFragmentManager().popBackStack();
	}

	@Override
	public void onClickSectionButton(int chapterId, int sectionId, int learningLevel) {
		showLearningContents(chapterId, sectionId, learningLevel);
	}

	@Override
	public void onClickPositiveButton() {
		uploadCurrentLearningLevelData();
	}

	private void uploadCurrentLearningLevelData() {
		Volleyer.volleyer().put(mRootUrl + "user/current-level").addHeader("Authorization", restoreSessionFromSharedPreferences())
				.addHeader("Content-Type", "application/json").withBody(makeJSONTypeCurrentLearningLevelData())
				.withListener(new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						finish();
					}
				}).withErrorListener(new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(MainActivity.this, "학습 데이터를 업로드를 실패하였습니다. 다시 한 번 앱을 종료해주세요.", Toast.LENGTH_SHORT).show();
			}
		}).execute();
	}

	private String makeJSONTypeCurrentLearningLevelData() {
		try {
			JSONObject currentLearningLevelData = new JSONObject();
			JSONArray currentLevels = new JSONArray();
			for (int i = 0; i < Preferences.LEARNING_CHAPTER_COUNT; i++) {
				JSONObject tempObject = new JSONObject();
				tempObject.put("chapter_id", mAbstractChapterInfo.get(i).chapter_id);
				tempObject.put("current_level", mAbstractChapterInfo.get(i).current_level);

				currentLevels.put(tempObject);
			}
			currentLearningLevelData.put("current_levels", currentLevels);
			return currentLearningLevelData.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void onClickNegativeButton() {}

	@Override
	public void onClickSelectionButton(final int index) {
		final int learningLevel = index + 1;
		final AbstractChapterInfo abstractChapterInfo = mAbstractChapterInfo.get(mCurrentChapterIndex);
		updateLearningChapterView(learningLevel,
				abstractChapterInfo.progress_info.get(index).section_title,
				abstractChapterInfo.progress_info.get(index).progress_percentage);
		mAbstractChapterInfo.get(mCurrentChapterIndex).current_level = learningLevel;

		mTempContinueChapterButton.setOnClickListener(null);
		mTempSeeSyllabusButton.setOnClickListener(null);

		mTempContinueChapterButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showLearningContents(abstractChapterInfo.progress_info.get(index).chapter_id,
						abstractChapterInfo.progress_info.get(index).current_section,
						learningLevel);
			}
		});

		mTempSeeSyllabusButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showSyllabus(abstractChapterInfo.progress_info.get(index).chapter_id,
						abstractChapterInfo.progress_info.get(index).current_section,
						learningLevel);
			}
		});
	}

	private void updateLearningChapterView(int learningLevel, String progressDetail, int progress) {
		mTempTextView.setText(configureLearningLevel(learningLevel));
		mTempTextView.setBackgroundResource(configureLearningLevelIcon(learningLevel));
		mTempChapterProgressTextView.setText(progressDetail);
		mTempCircleProgress.setProgress(progress);

		if (isNewStart(mAbstractChapterInfo.get(mCurrentChapterIndex).progress_info.get(learningLevel - 1).current_section)) {
			mTempContinueChapterButton.setText(R.string.start_learning);
		} else {
			mTempContinueChapterButton.setText(R.string.continue_learning);
		}

		if (progress == 100) {
			mTempContinueChapterButton.setText(R.string.finish_learning);
			mTempContinueChapterButton.setEnabled(false);
		} else {
			mTempContinueChapterButton.setEnabled(true);
		}
	}
}
