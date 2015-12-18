package com.skku.se;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.navercorp.volleyextensions.volleyer.Volleyer;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by XEiN on 12/3/15.
 */
public class AnswerDialogFragment extends DialogFragment {
	public static final String TAG = "AnswerDialogFragment";

	private static final String QNA_SECTION_NAME = "qna_section_name";
	private static final String QNA_DETAIL = "qna_detail";
	private static final String QNA_DATE = "qna_date";
	private static final String QNA_ID = "qna_id";
	private static final String QNA_LIST_POSITION = "qna_list_position";

	private String mQnASectionName;
	private String mQnADetail;
	private String mQnADate;
	private int mQnAId;
	private int mListPosition;

	private AnswerDialogFragmentCallback mAnswerDialogFragmentCallback;

	public static AnswerDialogFragment newInstance(String qnASectionName, String qnADetail, String qnADate, int qnAId, int position) {
		AnswerDialogFragment answerDialogFragment = new AnswerDialogFragment();
		Bundle args = new Bundle();
		args.putString(QNA_SECTION_NAME, qnASectionName);
		args.putString(QNA_DETAIL, qnADetail);
		args.putString(QNA_DATE, qnADate);
		args.putInt(QNA_ID, qnAId);
		args.putInt(QNA_LIST_POSITION, position);
		answerDialogFragment.setArguments(args);

		return answerDialogFragment;
	}


	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments() != null) {
			mQnASectionName = getArguments().getString(QNA_SECTION_NAME);
			mQnADetail = getArguments().getString(QNA_DETAIL);
			mQnADate = getArguments().getString(QNA_DATE);
			mQnAId = getArguments().getInt(QNA_ID);
			mListPosition = getArguments().getInt(QNA_LIST_POSITION);
		}
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_answer, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		configureToolbar(view);
		configureQuestionView(view);
		configureAnswerView(view);
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		return dialog;
	}

	private void configureToolbar(View rootView) {
		Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar_answer);
		toolbar.setTitle(R.string.answer_question);
		toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}

	private void configureQuestionView(View rootView) {
		TextView sectionNameTextView = (TextView) rootView.findViewById(R.id.textView_section_name);
		TextView qnADetailTextView = (TextView) rootView.findViewById(R.id.textView_qna_detail);
		TextView dateTextView = (TextView) rootView.findViewById(R.id.textView_date);

		Log.d(TAG, mQnASectionName + mQnADetail + mQnADate);

		sectionNameTextView.setText(mQnASectionName);
		qnADetailTextView.setText(mQnADetail);
		dateTextView.setText(mQnADate);
	}

	private void configureAnswerView(View rootView) {
		final EditText answerInputEditText = (EditText) rootView.findViewById(R.id.editText_answer);
		Button sendAnswerButton = (Button) rootView.findViewById(R.id.button_answer);

		sendAnswerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String answerString = answerInputEditText.getText().toString();
				if (answerString.length() > 0) {
					uploadAnswer(answerString);
				} else {
					Toast.makeText(getActivity(), "답변을 입력해주세요.", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	public void setAnswerDialogFragmentCallback(AnswerDialogFragmentCallback answerDialogFragmentCallback) {
		mAnswerDialogFragmentCallback = answerDialogFragmentCallback;
	}

	private void uploadAnswer(String answerString) {
		Volleyer.volleyer().put(getResources().getString(R.string.root_url) + "questions/" + mQnAId)
				.addHeader("Authorization", restoreSessionFromSharedPreferences()).addHeader("Content-Type", "application/json")
				.withBody(makeJSONTypeAnswerData(answerString)).withListener(new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				Toast.makeText(getActivity(), "답변 완료.", Toast.LENGTH_SHORT).show();
				dismiss();
				mAnswerDialogFragmentCallback.onClickSendAnswer(mListPosition);
			}
		}).withErrorListener(new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(getActivity(), "답변 업로드 실패", Toast.LENGTH_SHORT).show();
			}
		}).execute();
	}

	private String restoreSessionFromSharedPreferences() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		Log.d(TAG, String.valueOf(sharedPreferences.getInt(Preferences.SESSION_ID, -1)));
		return String.valueOf(sharedPreferences.getInt(Preferences.SESSION_ID, -1));
	}

	private String makeJSONTypeAnswerData(String answerString) {
		try {
			JSONObject temp = new JSONObject();
			temp.put("answer", answerString);
			Log.d(TAG, temp.toString());
			return temp.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public interface AnswerDialogFragmentCallback {
		void onClickSendAnswer(int position);
	}
}
