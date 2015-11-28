package com.skku.se;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.navercorp.volleyextensions.volleyer.Volleyer;
import com.skku.se.JacksonClass.AbstractSectionInfo;
import com.skku.se.JacksonClass.SyllabusInfo;

public class SyllabusFragment extends Fragment {
	private static final String TAG = "SyllabusFragment";

	private static final String ARG_PARAM_CHAPTER_ID = "chapter_id";
	private static final String ARG_PARAM_LEARNING_LEVEL = "learning_level";

	private LinearLayout mSyllabusLinearLayout;
	private Toolbar mToolbar;

	private int mChapterId;
	private int mLearningLevel;

	private SyllabusFragmentCallback mSyllabusFragmentCallback;

	/**
	 * Fragment 생성하기 위한 static 메소드
	 *
	 * @param chapterId chapter 아이디
	 * @param level     학습 레벨
	 * @return A new instance of fragment SyllabusFragment.
	 */
	public static SyllabusFragment newInstance(int chapterId, int level) {
		SyllabusFragment fragment = new SyllabusFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PARAM_CHAPTER_ID, chapterId);
		args.putInt(ARG_PARAM_LEARNING_LEVEL, level);
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
			mChapterId = getArguments().getInt(ARG_PARAM_CHAPTER_ID);
			mLearningLevel = getArguments().getInt(ARG_PARAM_LEARNING_LEVEL);
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

		mSyllabusLinearLayout = (LinearLayout) view.findViewById(R.id.linearLayout_syllabus);

		downloadSyllabusContent(mChapterId, mLearningLevel);
	}

	private void configureSectionRow(SyllabusInfo syllabusInfo) {
		for (int i = 0; i < syllabusInfo.sections.size(); i++) {
			final AbstractSectionInfo abstractSectionInfo = syllabusInfo.sections.get(i);

			View sectionRowView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_section_row, mSyllabusLinearLayout, false);

			TextView sectionNumberIconTextView = (TextView) sectionRowView.findViewById(R.id.textView_section_number_icon);
			sectionNumberIconTextView.setText(String.valueOf(abstractSectionInfo.sectionId));

			TextView sectionNameTextView = (TextView) sectionRowView.findViewById(R.id.textView_section_name);

			sectionNameTextView.setText(abstractSectionInfo.sectionTitle);

			sectionRowView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mSyllabusFragmentCallback.onClickSectionButton(mChapterId, abstractSectionInfo.sectionId, mLearningLevel);
				}
			});

			mSyllabusLinearLayout.addView(sectionRowView);
		}
	}

	private void configureToolbar(View rootView) {
		mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar_syllabus);
		mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
		mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
					getActivity().getWindow().setStatusBarColor(getResources().getColor(android.R.color.transparent));
				} else {
					getActivity().getWindow().setStatusBarColor(getActivity().getColor(android.R.color.transparent));
				}
				mSyllabusFragmentCallback.onClickNavigationBackButton();
			}
		});
	}


	private void downloadSyllabusContent(int chapterId, int level) {
		Volleyer.volleyer().get(getResources().getString(R.string.root_url) + "chapters/" + chapterId + "/levels/" + level + "/sections")
				.withTargetClass(SyllabusInfo.class).withListener(new Response.Listener<SyllabusInfo>() {
			@Override
			public void onResponse(SyllabusInfo response) {
				mToolbar.setTitle(response.chapterTitle);
				if (mLearningLevel == 1) {
					mToolbar.setSubtitle("Beginner class");
				} else {
					mToolbar.setSubtitle("Intermediate class");
				}
				configureSectionRow(response);
			}
		}).withErrorListener(new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(getActivity(), "정보 가져오기를 실패하였습니다.", Toast.LENGTH_SHORT).show();
			}
		}).execute();
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

		void onClickSectionButton(int chapterId, int sectionId, int learningLevel);
	}
}
