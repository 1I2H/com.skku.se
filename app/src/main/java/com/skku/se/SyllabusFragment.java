package com.skku.se;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SyllabusFragment extends Fragment {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM_CHAPTER_NAME = "chapter_name";
	private static final String ARG_PARAM_CHAPTER_NUMBER = "chapter_number";

	private String mChapterTitle;
	private int mChapterNumber;

	private SyllabusFragmentCallback mSyllabusFragmentCallback;

	/**
	 * Fragment 생성하기 위한 static 메소드
	 *
	 * @param chapterTitle  chapter 제목
	 * @param chapterNumber chapter 번호
	 * @return A new instance of fragment SyllabusFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static SyllabusFragment newInstance(String chapterTitle, int chapterNumber) {
		SyllabusFragment fragment = new SyllabusFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM_CHAPTER_NAME, chapterTitle);
		args.putInt(ARG_PARAM_CHAPTER_NUMBER, chapterNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public SyllabusFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mChapterTitle = getArguments().getString(ARG_PARAM_CHAPTER_NAME);
			mChapterNumber = getArguments().getInt(ARG_PARAM_CHAPTER_NUMBER);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_syllabus, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		configureToolbar(view);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
		}

		LinearLayout syllabusLinearLayout = (LinearLayout) view.findViewById(R.id.linearLayout_syllabus);

		// TODO change the hard-coded string
		String[] syllabus;
		if (mChapterNumber == MainActivity.STRUCTURE_CHAPTER_NUMBER) {
			String[] structureSyllabus = {"기본 개념 정의 및 이해", "구조체 선언 및 초기화", "구조체 member 접근", "구조체 포인터", "함수와 할당"};
			syllabus = structureSyllabus;
		} else {
			String[] otherSyllabus = {""};
			syllabus = otherSyllabus;
		}

		for (int i = 0; i < syllabus.length; i++) {
			View sectionRowView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_section_row, syllabusLinearLayout, false);

			TextView sectionNumberIconTextView = (TextView) sectionRowView.findViewById(R.id.textView_section_number_icon);
			sectionNumberIconTextView.setText("" + (i + 1));

			final int sectionNumber = i + 1;

			TextView sectionNameTextView = (TextView) sectionRowView.findViewById(R.id.textView_section_name);

			sectionNameTextView.setText(syllabus[i]);

			sectionRowView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					showLearningContents(mChapterNumber, sectionNumber);
				}
			});

			syllabusLinearLayout.addView(sectionRowView);
		}
	}

	private void configureToolbar(View rootView) {
		Toolbar mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar_syllabus);
		mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
		mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					getActivity().getWindow().setStatusBarColor(getResources().getColor(android.R.color.transparent));
				}
				mSyllabusFragmentCallback.onClickNavigationBackButton();
			}
		});
		mToolbar.setTitle(mChapterTitle);
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

	private void callLearningActivity(String chapterName, String sectionName) {
		Intent intent = new Intent(getActivity(), LearningActivity.class);
		intent.putExtra(MainActivity.CHAPTER_NAME, chapterName);
		intent.putExtra(MainActivity.SECTION_NAME, sectionName);
		startActivity(intent);
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		try {
			mSyllabusFragmentCallback = (SyllabusFragmentCallback) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString() + " must implement SyllabusFragmentCallback");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mSyllabusFragmentCallback = null;
	}

	public interface SyllabusFragmentCallback {
		void onClickNavigationBackButton();
	}
}
