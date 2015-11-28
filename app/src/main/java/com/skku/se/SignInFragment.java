package com.skku.se;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.navercorp.volleyextensions.volleyer.Volleyer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignInFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignInFragment extends Fragment implements View.OnClickListener {
	private static final String TAG = "SignInFragment";

	private EditText mUserIdEditText;
	private EditText mUserPasswordEditText;
	private Button mSignInButton;
	private Button mSignUpButton;

	private SignInFragmentCallback mSignInFragmentCallback;

	public SignInFragment() {
		// Required empty public constructor
	}

	public static SignInFragment newInstance() {
		SignInFragment fragment = new SignInFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_sign_in, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		initViewInstance(view);
		setViewListeners();
	}

	private void initViewInstance(View rootView) {
		mUserIdEditText = (EditText) rootView.findViewById(R.id.editText_id);
		mUserPasswordEditText = (EditText) rootView.findViewById(R.id.editText_password);
		mSignInButton = (Button) rootView.findViewById(R.id.button_sign_in);
		mSignUpButton = (Button) rootView.findViewById(R.id.button_go_sign_up_page);
	}

	private void setViewListeners() {
		mSignInButton.setOnClickListener(this);
		mSignUpButton.setOnClickListener(this);
	}

	private boolean isUserInputDataValid() {
		String emailAddress = mUserIdEditText.getText().toString();
		if (isValidEmail(emailAddress)) {
			return !emailAddress.isEmpty() && !mUserPasswordEditText.getText().toString().isEmpty();
		} else {
			Toast.makeText(getActivity(), "잘 못된 이메일 형식입니다.", Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	private JSONObject makeJSONTypeUserInputData() {
		try {
			JSONObject userInputData = new JSONObject();
			userInputData.put("email", mUserIdEditText.getText().toString());
			userInputData.put("password", mUserPasswordEditText.getText().toString());

			return userInputData;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	private boolean isValidEmail(String email) {
		boolean err = false;

		String regex = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(email);

		if (m.matches()) {
			err = true;
		}
		return err;
	}

	private void inquireUserInfoFromServer() {
		if (isUserInputDataValid()) {
			mSignInFragmentCallback.startProgressBar();
			Volleyer.volleyer().post(getResources().getString(R.string.root_url) + "user/session").addHeader("Content-Type", "application/json")
					.withBody(makeJSONTypeUserInputData().toString()).withListener(new Response.Listener<String>() {
				@Override
				public void onResponse(String response) {
					mSignInFragmentCallback.stopProgressBar();
					try {
						Log.d(TAG, response.toString());
						saveSessionInSharedPreference((new JSONObject(response).optInt("userId")));
						callMainActivity();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}).withErrorListener(new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					mSignInFragmentCallback.stopProgressBar();
					Toast.makeText(getActivity(), "로그인에 실패하였습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
				}
			}).execute();
		}
	}

	private void saveSessionInSharedPreference(int userId) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		if (sharedPreferences.getInt(Preferences.SESSION_ID, -1) == -1) {
			sharedPreferences.edit().putInt(Preferences.SESSION_ID, userId).apply();
		}
	}

	private void callMainActivity() {
		Intent intent = new Intent(getActivity(), MainActivity.class);
		startActivity(intent);
		getActivity().finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.button_sign_in:
				inquireUserInfoFromServer();
				break;
			case R.id.button_go_sign_up_page:
				mSignInFragmentCallback.onClickGoSignUpPageButton();
				break;
		}
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		try {
			mSignInFragmentCallback = (SignInFragmentCallback) context;
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mSignInFragmentCallback = null;
	}

	public interface SignInFragmentCallback {
		void onClickGoSignUpPageButton();

		void startProgressBar();

		void stopProgressBar();
	}
}
