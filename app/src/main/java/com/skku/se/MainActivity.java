package com.skku.se;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.CircleProgress;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements SyllabusFragment.SyllabusFragmentCallback,
		AlertDialogFragment.AlertDialogFragmentCallback {
	private static final String TAG = "MainActivity";

	public static final int FILE_INPUT_OUTPUT_CHAPTER_NUMBER = 1;
	public static final int STRUCTURE_CHAPTER_NUMBER = 2;
	public static final int COMPILE_FLOW_CHAPTER_NUMBER = 3;

	public static final String CHAPTER_NAME = "chapter_name";
	public static final String SECTION_NAME = "section_name";

	private LinearLayout mLearningChapterListLinearLayout;
	private LinearLayout mQnALinearLayout;

	private JSONArray mLearningInfo;
	private JSONArray mQnAList;

	private CircleProgress mTempCircleProgress;
	private TextView mTempChapterProgressTextView;
	private TextView mTempTextView;
	private JSONObject mTempLearningChapterContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		/* hard-coded part */
		JSONObject overAllContents = MockServer.getInstance(this).makeOverallContents().getOverAllContents();
		mLearningInfo = overAllContents.optJSONArray("learningInfo");
		mQnAList = overAllContents.optJSONArray("qnA");

		configureLearningList();
		configureQnAList();
	}

	@Override
	public void onBackPressed() {
		if (getSupportFragmentManager().getBackStackEntryCount() >= 1) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				getWindow().setStatusBarColor(getResources().getColor(android.R.color.transparent));
			}
		}
		super.onBackPressed();
	}

	private void configureLearningList() {
		configureLearningChapterList();
		configureQnAList();
	}

	private void configureLearningLevelView() {

	}

	private void configureLearningChapterList() {
		mLearningChapterListLinearLayout = (LinearLayout) findViewById(R.id.linearLayout_learning_chapter_list);

		try {
			for (int i = 0; i < mLearningInfo.length(); i++) {
				View chapterRow = inflateChapterRow();
				configureChapterRow(chapterRow, i);
				mLearningChapterListLinearLayout.addView(chapterRow);
			}
		} catch (JSONException e) {
			e.printStackTrace();
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

	private void configureChapterRow(View chapterRow, int index) throws JSONException {
		final CircleProgress circleProgress = (CircleProgress) chapterRow.findViewById(R.id.circle_progress_chapter);
		TextView chapterTitleTextView = (TextView) chapterRow.findViewById(R.id.textView_chapter_title);
		final TextView chapterProgressTextView = (TextView) chapterRow.findViewById(R.id.textView_chapter_progress);
		final Button continueChapterButton = (Button) chapterRow.findViewById(R.id.button_continue_chapter);
		final Button seeSectionsChapterButton = (Button) chapterRow.findViewById(R.id.button_see_sections_chapter);
		final TextView learningLevelChapterTextView = (TextView) chapterRow.findViewById(R.id.textView_learning_level_chapter);
		ImageView configureLearningLevelChapterImageView = (ImageView) chapterRow.findViewById(R.id.imageView_configure_learning_level_chapter);

		///////////////////
		final JSONObject learningChapterContent = mLearningInfo.optJSONObject(index);

		final int chapterNumber = index + 1;
		final int currentSectionNumber;
		final String chapterTitle = learningChapterContent.optString("title");
		final float progress;

		if (learningChapterContent.getInt("level") == 1) {
			learningLevelChapterTextView.setText("Beginner");
			chapterProgressTextView.setText(learningChapterContent.getString("beginnerProgressDetail"));

			currentSectionNumber = learningChapterContent.optInt("beginnerCurrentSection");
			progress = (float) learningChapterContent.optInt("beginnerCurrentSection") /
					(float) learningChapterContent.optInt("beginnerSectionCount") * 100;
		} else {
			learningLevelChapterTextView.setText("Intermediate");
			learningLevelChapterTextView.setBackgroundResource(R.drawable.background_intermediate_level_icon);
			chapterProgressTextView.setText(learningChapterContent.getString("intermediateProgressDetail"));

			currentSectionNumber = learningChapterContent.optInt("intermediateCurrentSection");
			progress = (float) learningChapterContent.optInt("intermediateCurrentSection") /
					(float) learningChapterContent.optInt("intermediateSectionCount") * 100;
		}

		circleProgress.setProgress((int) progress);
		chapterTitleTextView.setText(chapterTitle);

		continueChapterButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showLearningContents(chapterNumber, currentSectionNumber);
			}
		});
		seeSectionsChapterButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showSyllabus(chapterTitle, chapterNumber);
			}
		});
		configureLearningLevelChapterImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//Toast.makeText(v.getContext(), "LEVEL CHANGE", Toast.LENGTH_SHORT).show();
				mTempLearningChapterContent = learningChapterContent;
				mTempCircleProgress = circleProgress;
				mTempTextView = learningLevelChapterTextView;
				mTempChapterProgressTextView = chapterProgressTextView;

				AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
				alertDialogFragment.setDialogStyle(AlertDialogFragment.RADIO_BUTTON_STYLE);
				alertDialogFragment.setTitle(R.string.select_learning_level);
				alertDialogFragment.setSelectionStringArray(R.array.learning_level_list);
				alertDialogFragment.show(getSupportFragmentManager(), "alertDialog");
			}
		});
	}

	private void configureQnAList() {
		/* Q&A 목록 설정 */
		mQnALinearLayout = (LinearLayout) findViewById(R.id.linearLayout_qna);

		for (int i = 0; i < mQnAList.length(); i++) {
			// 질문과 답의 아이디를 비교하여 비슷하면 질문 바로 밑에 답변을 출력하도록 짜야함
			// 현재 서버 API가 완성되지 않았기 때문에 간단하게 코드만 작성
			JSONObject qnAContent = mQnAList.optJSONObject(i);

			View questionRow = inflateQnARow(true);
			configureQnARow(questionRow, qnAContent, true);
			mQnALinearLayout.addView(questionRow);

			if (!qnAContent.isNull("answerDate")) {
				View answerRow;
				answerRow = inflateQnARow(false);
				configureQnARow(answerRow, qnAContent, false);
				mQnALinearLayout.addView(answerRow);
			}
		}
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

	private void configureQnARow(View qnARow, JSONObject qnAContent, boolean isQuestion) {
		TextView qnAIconTextView = (TextView) qnARow.findViewById(R.id.textView_QnA_icon);
		TextView qnASectionNameTextView = (TextView) qnARow.findViewById(R.id.textView_section_name);
		TextView qnADetailTextView = (TextView) qnARow.findViewById(R.id.textView_qna_detail);
		TextView qnADateTextView = (TextView) qnARow.findViewById(R.id.textView_date);

		if (isQuestion) {
			qnAIconTextView.setText("Q");
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
				qnAIconTextView.setBackground(getResources().getDrawable(R.drawable.background_question_icon));
			} else {
				qnAIconTextView.setBackground(getDrawable(R.drawable.background_question_icon));
			}
			qnADetailTextView.setText(qnAContent.optString("question"));
			qnADateTextView.setText(qnAContent.optString("questionDate"));
		} else {
			qnAIconTextView.setText("A");
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
				qnAIconTextView.setBackground(getResources().getDrawable(R.drawable.background_answer_icon));
			} else {
				qnAIconTextView.setBackground(getDrawable(R.drawable.background_answer_icon));
			}
			qnADetailTextView.setText(qnAContent.optString("answer"));
			qnADateTextView.setText(qnAContent.optString("answerDate"));
		}
		qnASectionNameTextView.setText(qnAContent.optString("sectionName"));
	}

	private float computeLearningProgress(int currentSectionNumber, int sectionNumber) {
		return (float) currentSectionNumber / (float) sectionNumber * 100;
	}

	private void updateLearningChapterView(String level, int levelIconResId, String progressDetail, float progress) {
		mTempTextView.setText(level);
		mTempTextView.setBackgroundResource(levelIconResId);
		mTempChapterProgressTextView.setText(progressDetail);
		mTempCircleProgress.setProgress((int) progress);
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
		// TODO sign out from the server
		callLoginActivity();
	}

	private void showLearningContents(int chapterNumber, int sectionNumber) {
		String[] names = downloadSectionLearningContents(chapterNumber, sectionNumber);
		callLearningActivity(names[0], names[1]);
	}

	private String[] downloadSectionLearningContents(int chapterNumber, int sectionNumber) {
		// TODO get learning contents of @param sectionNumber of @param chapterNumber from the server

		// names[0] is for chapter name and names[1] is for section name
		String[] names = new String[2];

		if (chapterNumber == 1) {
			names[0] = "파일 입/출력";
		} else if (chapterNumber == 2) {
			names[0] = "구조체";
		} else {
			names[0] = "컴파일 과정";
		}

		switch (sectionNumber) {
			case 1:
				names[1] = "Section_1";
				break;
			case 2:
				names[1] = "Section_2";
				break;
			case 3:
				names[1] = "Section_3";
				break;
			case 4:
				names[1] = "Section_4";
				break;
			case 5:
				names[1] = "Section_5";
				break;
			default:
				names[1] = "Section_X";
				break;
		}

		return names;
	}

	private void showSyllabus(String chapterName, int chapterNumber) {
		SyllabusFragment syllabusFragment = SyllabusFragment.newInstance(chapterName, chapterNumber);
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(android.R.id.content, syllabusFragment);
		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
	}

	private void callLearningActivity(String chapterName, String sectionName) {
		Intent intent = new Intent(MainActivity.this, LearningActivity.class);
		intent.putExtra(CHAPTER_NAME, chapterName);
		intent.putExtra(SECTION_NAME, sectionName);
		startActivity(intent);
	}

	private void callLoginActivity() {
		Intent intent = new Intent(MainActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void onClickNavigationBackButton() {
		getSupportFragmentManager().popBackStack();
	}

	@Override
	public void onClickPositiveButton() {

	}

	@Override
	public void onClickNegativeButton() {

	}

	@Override
	public void onClickSelectionButton(int index) {
		String levelTextString = getResources().getStringArray(R.array.learning_level_list)[index];
		try {
			if (index == 0) {
				updateLearningChapterView(levelTextString,
						R.drawable.background_beginner_level_icon,
						mTempLearningChapterContent.getString("beginnerProgressDetail"),
						computeLearningProgress(mTempLearningChapterContent.optInt("beginnerCurrentSection"),
								mTempLearningChapterContent.optInt("beginnerSectionCount")));
			} else {
				updateLearningChapterView(levelTextString,
						R.drawable.background_intermediate_level_icon,
						mTempLearningChapterContent.getString("intermediateProgressDetail"),
						computeLearningProgress(mTempLearningChapterContent.optInt("intermediateCurrentSection"),
								mTempLearningChapterContent.optInt("intermediateSectionCount")));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
