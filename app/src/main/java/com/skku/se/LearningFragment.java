package com.skku.se;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.navercorp.volleyextensions.volleyer.Volleyer;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by XEiN on 11/27/15.
 */
public class LearningFragment extends Fragment {
	private static final String PAGE_TYPE = "page_type";
	private static final String PAGE_LEARNING_CONTENT_HEAD_DATA = "page_head_data";
	private static final int LEARNING_TEMPLATE_ONE = 1;
	private static final int LEARNING_TEMPLATE_TWO = 2;
	private static final int LEARNING_TEMPLATE_THREE = 3;

	private int mPageType;
	private int mChapterId;
	private int mLearningLevel;
	private int mSectionId;
	private String mSectionTitle;

	/**
	 * Create a new instance of CountingFragment, providing "num"
	 * as an argument.
	 */
	public static LearningFragment newInstance(int pageType, Bundle pageLearningContentHeadData) {
		LearningFragment learningFragment = new LearningFragment();
		Bundle args = new Bundle();
		args.putInt(PAGE_TYPE, pageType);
		args.putBundle(PAGE_LEARNING_CONTENT_HEAD_DATA, pageLearningContentHeadData);
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
		Bundle pageLearningContents = getArguments() != null ? getArguments().getBundle(PAGE_LEARNING_CONTENT_HEAD_DATA) : null;

		mChapterId = pageLearningContents.getInt("chapter_id");
		mLearningLevel = pageLearningContents.getInt("level");
		mSectionId = pageLearningContents.getInt("section_id");
		mSectionTitle = pageLearningContents.getString("section_title");
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
				break;
			case LEARNING_TEMPLATE_TWO:
				templateView = inflater.inflate(R.layout.fragment_learning_template_two, container, false);
				break;
			case LEARNING_TEMPLATE_THREE:
				templateView = inflater.inflate(R.layout.fragment_learning_template_three, container, false);
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
		TextView learningContentsTextView = (TextView) templateView.findViewById(R.id.textView_learning_contents_one);
		learningContentsTextView.setText(((LearningActivity) getActivity()).getSectionInfo().content.section_detail1);
	}

	private void configureLearningTemplateTwoView(View templateView) {
		TextView learningContentsTextView = (TextView) templateView.findViewById(R.id.textView_learning_contents_two);
		TextView problemTextView = (TextView) templateView.findViewById(R.id.textView_problem);
		RadioGroup problemSelectionRadioGroup = (RadioGroup) templateView.findViewById(R.id.radioGroup_problem);

		final EditText askQuestionEditText = (EditText) templateView.findViewById(R.id.editText_ask);
		Button askQuestionButton = (Button) templateView.findViewById(R.id.button_ask);

		learningContentsTextView.setText(((LearningActivity) getActivity()).getSectionInfo().content.section_detail2);
		problemTextView.setText(((LearningActivity) getActivity()).getSectionInfo().question.question);

		if (((LearningActivity) getActivity()).getSectionInfo().content.chapter_id == 3) {
			ImageView compileProgressImageView = (ImageView) templateView.findViewById(R.id.imageView_compile_process);
			compileProgressImageView.setVisibility(View.VISIBLE);
		}

		for (int i = 0; i < 5; i++) {
			((RadioButton) problemSelectionRadioGroup.getChildAt(i))
					.setText(((LearningActivity) getActivity()).getSectionInfo().question.choices.get(i).choice_detail);
		}

		final int correctAnswerRadioButtonResId = configureCorrectAnswerRadioButton(((LearningActivity) getActivity())
				.getSectionInfo().question.answer_no);

		problemSelectionRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (correctAnswerRadioButtonResId == checkedId) {
					Toast.makeText(getActivity(), "정답입니다!", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getActivity(), "틀렸습니다...ㅠㅠ다시 풀어보세요~", Toast.LENGTH_SHORT).show();
				}
			}
		});

		askQuestionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String questionString = askQuestionEditText.getText().toString();
				if (questionString.length() > 0) {
					uploadQuestion(askQuestionEditText.getText().toString());
				} else {
					Toast.makeText(getActivity(), "질문을 입력해주세요.", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private int configureCorrectAnswerRadioButton(int answerNumber) {
		switch (answerNumber) {
			case 1:
				return R.id.radioButton_answer_one;
			case 2:
				return R.id.radioButton_answer_two;
			case 3:
				return R.id.radioButton_answer_three;
			case 4:
				return R.id.radioButton_answer_four;
			case 5:
				return R.id.radioButton_answer_five;
			default:
				return R.id.radioButton_answer_one;
		}
	}

	private void configureLearningTemplateThreeView(View templateView) {
		TextView learningContentsTextView = (TextView) templateView.findViewById(R.id.textView_learning_contents_three);
		WebView practicalWebView = (WebView) templateView.findViewById(R.id.webView_practical);

		learningContentsTextView.setText(((LearningActivity) getActivity()).getSectionInfo().content.section_detail3);

		if (((LearningActivity) getActivity()).getSectionInfo().content.chapter_id == 2) {
			practicalWebView.setVisibility(View.VISIBLE);
			// 실습창을 위한 webview
			practicalWebView.loadUrl("http://cpp.sh/");
			practicalWebView.getSettings().setJavaScriptEnabled(true);
			practicalWebView.setVerticalScrollBarEnabled(true);
		}
	}

	private void uploadQuestion(String questionString) {
		Volleyer.volleyer().post(getResources().getString(R.string.root_url) + "question")
				.addHeader("Authorization", restoreSessionFromSharedPreferences()).addHeader("Content-Type", "application/json")
				.withBody(makeJSONTypeQuestionData(questionString)).withListener(new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				Toast.makeText(getActivity(), "질문이 등록되었습니다.", Toast.LENGTH_SHORT).show();
			}
		}).withErrorListener(new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(getActivity(), "질문 등록을 실패하였습니다.", Toast.LENGTH_SHORT).show();
			}
		}).execute();
	}

	private String makeJSONTypeQuestionData(String questionString) {
		try {
			JSONObject questionData = new JSONObject();
			questionData.put("section_title", mSectionTitle);
			questionData.put("question", questionString);

			return questionData.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String restoreSessionFromSharedPreferences() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		int userId = sharedPreferences.getInt(Preferences.SESSION_ID, -1);
		if (userId != -1) {
			return String.valueOf(sharedPreferences.getInt(Preferences.SESSION_ID, -1));
		} else {
			Toast.makeText(getActivity(), "세션이 만료되었습니다.", Toast.LENGTH_SHORT).show();
			restartApplication();
			return null;
		}
	}

	private void restartApplication() {
		Intent intent = new Intent(getActivity(), LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		getActivity().finish();
	}

	@Override
	public void onViewCreated(View templateView, Bundle savedInstanceState) {
		super.onViewCreated(templateView, savedInstanceState);
		switch (mPageType) {
			case LEARNING_TEMPLATE_ONE:
				configureLearningTemplateOneView(templateView);
				break;
			case LEARNING_TEMPLATE_TWO:
				configureLearningTemplateTwoView(templateView);
				break;
			case LEARNING_TEMPLATE_THREE:
				configureLearningTemplateThreeView(templateView);
				break;
			default:
				break;
		}
	}
}
