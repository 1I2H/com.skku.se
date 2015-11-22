package com.skku.se;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.CircleProgress;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SyllabusFragment.SyllabusFragmentCallback {
	private static final String TAG = "MainActivity";

	private static final int FILE_INPUT_OUTPUT_CHAPTER_NUMBER = 1;
	private static final int STRUCTURE_CHAPTER_NUMBER = 2;
	private static final int COMPILE_FLOW_CHAPTER_NUMBER = 3;

	public static final String CHAPTER_NAME = "chapter_name";
	public static final String SECTION_NAME = "section_name";

	private TextView mFileInputOutputProgressTextView;
	private TextView mStructureProgressTextView;
	private TextView mCompileFlowProgressTextView;

	private CircleProgress mFileInputOutputCircleProgress;
	private CircleProgress mStructureCircleProgress;
	private CircleProgress mCompileFlowCircleProgress;

	private Button mContinueFileInputOutputButton;
	private Button mContinueStructureButton;
	private Button mContinueCompileFlowCircleButton;

	private Button mSeeFileInputOutputSyllabusButton;
	private Button mSeeStructureSyllabusButton;
	private Button mSeeCompileFlowSyllabusButton;

	private LinearLayout mQnALinearLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
			}
		});

		configureLearningList();
		configureQnAList();
	}

	private void configureLearningList() {
		// 파일 입/출력 부분 학습 진행 현황 텍스트 GUI
		mFileInputOutputProgressTextView = (TextView) findViewById(R.id.textView_file_input_output_progress);
		mFileInputOutputProgressTextView.setText("미실시");

		// 구조체 학습 진행 현황 텍스트 GUI
		mStructureProgressTextView = (TextView) findViewById(R.id.textView_structure_progress);
		mStructureProgressTextView.setText("미실시");

		// 컴파일 과정 학습 진행 현황 텍스트 GUI
		mCompileFlowProgressTextView = (TextView) findViewById(R.id.textView_compile_flow_progress);
		mCompileFlowProgressTextView.setText("미실시");

		// 파일 입/출력 진행도 GUI 객채화 및 출력할 %값 설정
		mFileInputOutputCircleProgress = (CircleProgress) findViewById(R.id.circle_progress_file_input_output);
		mFileInputOutputCircleProgress.setMax(100);
		mFileInputOutputCircleProgress.setProgress(40);

		// 구조체 진행도 GUI 객체화 및 출력할 %값 설정
		mStructureCircleProgress = (CircleProgress) findViewById(R.id.circle_progress_structure);
		mStructureCircleProgress.setMax(100);
		mStructureCircleProgress.setProgress(75);

		// 컴파일 과정 GUI 객채화 및 출력할 %값 설정
		mCompileFlowCircleProgress = (CircleProgress) findViewById(R.id.circle_progress_compile_flow);
		mCompileFlowCircleProgress.setMax(100);
		mCompileFlowCircleProgress.setProgress(25);

		// 파일 입/출력 이어하기 버튼 설정
		mContinueFileInputOutputButton = (Button) findViewById(R.id.button_continue_file_input_output);
		mContinueFileInputOutputButton.setOnClickListener(this);

		// 구조체 이어하기 버튼 설정
		mContinueStructureButton = (Button) findViewById(R.id.button_continue_structure);
		mContinueStructureButton.setOnClickListener(this);

		// 컴파일 과정 이어하기 버튼 설정
		mContinueCompileFlowCircleButton = (Button) findViewById(R.id.button_continue_compile_flow);
		mContinueCompileFlowCircleButton.setOnClickListener(this);

		// 파일 입/출력 학습 목록 보기 버튼 설정
		mSeeFileInputOutputSyllabusButton = (Button) findViewById(R.id.button_see_sections_file_input_output);
		mSeeFileInputOutputSyllabusButton.setOnClickListener(this);

		// 구조체 학습 목롭 보기 버튼 설정
		mSeeStructureSyllabusButton = (Button) findViewById(R.id.button_see_sections_structure);
		mSeeStructureSyllabusButton.setOnClickListener(this);

		// 컴파일 과정 이어하기 버튼 설정
		mSeeCompileFlowSyllabusButton = (Button) findViewById(R.id.button_see_sections_compile_flow);
		mSeeCompileFlowSyllabusButton.setOnClickListener(this);
	}

	private void configureQnAList() {
		/* Q&A 목록 설정 */
		mQnALinearLayout = (LinearLayout) findViewById(R.id.linearLayout_qna);

		int numberOfQuestion = 5;
		int numberOfAnswer = 3;

		for (int i = 0; i < numberOfQuestion + numberOfAnswer; i++) {
			// 질문과 답의 아이디를 비교하여 비슷하면 질문 바로 밑에 답변을 출력하도록 짜야함
			// 현재 서버 API가 완성되지 않았기 때문에 간단하게 코드만 작성
			View qnARow;
			if (i % 2 == 0) {
				qnARow = inflateQnARow(true);
			} else {
				qnARow = inflateQnARow(false);
			}

			TextView qnAIconTextView = (TextView) qnARow.findViewById(R.id.textView_QnA_icon);
			TextView qnASectionNameTextView = (TextView) qnARow.findViewById(R.id.textView_section_name);
			TextView qnADetailTextView = (TextView) qnARow.findViewById(R.id.textView_qna_detail);
			TextView qnADateTextView = (TextView) qnARow.findViewById(R.id.textView_date);

			if (i % 2 == 0) {
				qnAIconTextView.setText("Q");
				qnAIconTextView.setBackground(getResources().getDrawable(R.drawable.background_question_icon));
			} else {
				qnAIconTextView.setText("A");
				qnAIconTextView.setBackground(getResources().getDrawable(R.drawable.background_answer_icon));
			}
			mQnALinearLayout.addView(qnARow);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.button_continue_file_input_output:
				showLearningContents(1, 1);
				break;
			case R.id.button_continue_structure:
				showLearningContents(2, 1);
				break;
			case R.id.button_continue_compile_flow:
				showLearningContents(3, 1);
				break;
			case R.id.button_see_sections_file_input_output:
				showSyllabus("파일 입/출력", FILE_INPUT_OUTPUT_CHAPTER_NUMBER);
				break;
			case R.id.button_see_sections_structure:
				showSyllabus("구조체", STRUCTURE_CHAPTER_NUMBER);
				break;
			case R.id.button_see_sections_compile_flow:
				showSyllabus("컴파일 과정", COMPILE_FLOW_CHAPTER_NUMBER);
				break;
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
	public void onFragmentInteraction(Uri uri) {

	}
}
