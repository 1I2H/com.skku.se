package com.skku.se;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
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
 * Use the {@link SignUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpFragment extends Fragment implements View.OnClickListener, AlertDialogFragment.AlertDialogFragmentCallback {
	private static final String TAG = "SignUpFragment";

	private Toolbar mToolbar;
	private EditText mSignUpUserIdEditText;
	private EditText mSignUpPasswordEditText;
	private Button mSignUpButton;

	private SignUpFragmentCallback mSignUpFragmentCallback;

	public SignUpFragment() {
		// Required empty public constructor
	}

	public static SignUpFragment newInstance() {
		SignUpFragment fragment = new SignUpFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_sign_up, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		initViewInstance(view);
		setViewListeners();
	}

	private void initViewInstance(View rootView) {
		mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar_sign_up);
		mToolbar.setTitle(R.string.sign_up);
		mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
		mSignUpUserIdEditText = (EditText) rootView.findViewById(R.id.editText_id_sign_up);
		mSignUpPasswordEditText = (EditText) rootView.findViewById(R.id.editText_password_sign_up);
		mSignUpButton = (Button) rootView.findViewById(R.id.button_sign_up);
	}

	private void setViewListeners() {
		mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mSignUpFragmentCallback.onClickNavigationBackButton();
			}
		});
		mSignUpButton.setOnClickListener(this);
	}

	private boolean isUserInputDataValid() {
		String emailAddress = mSignUpUserIdEditText.getText().toString();
		if (isValidEmail(emailAddress)) {
			return !emailAddress.isEmpty() && !mSignUpPasswordEditText.getText().toString().isEmpty();
		} else {
			Toast.makeText(getActivity(), "잘 못된 이메일 형식입니다.", Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	private JSONObject makeJSONTypeUserInputData() {
		try {
			JSONObject userInputData = new JSONObject();
			userInputData.put("email", mSignUpUserIdEditText.getText().toString());
			userInputData.put("password", mSignUpPasswordEditText.getText().toString());

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

	private void uploadSignUpUserData() {
		if (isUserInputDataValid()) {
			mSignUpFragmentCallback.startProgressBar();
			Volleyer.volleyer().post(getResources().getString(R.string.root_url) + "user").addHeader("Content-Type", "application/json")
					.withBody(makeJSONTypeUserInputData().toString()).withListener(new Response.Listener<String>() {
				@Override
				public void onResponse(String response) {
					mSignUpFragmentCallback.stopProgressBar();
					try {
						saveSessionInSharedPreference((new JSONObject(response).optInt("userId")));
						saveUserIDAndPasswordInSharedPreference();
						Log.d(TAG, response.toString());
						showWelcomeDialog();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}).withErrorListener(new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					mSignUpFragmentCallback.stopProgressBar();
					Toast.makeText(getActivity(), "회원가입에 실패하였습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
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

	private void saveUserIDAndPasswordInSharedPreference() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		if (sharedPreferences.getString(Preferences.USER_ID, "").length() == 0 &&
				sharedPreferences.getString(Preferences.USER_PASSWORD, "").length() == 0) {
			sharedPreferences.edit().putString(Preferences.USER_ID, mSignUpUserIdEditText.getText().toString()).apply();
			sharedPreferences.edit().putString(Preferences.USER_PASSWORD, mSignUpPasswordEditText.getText().toString()).apply();
		}
	}

	private void showWelcomeDialog() {
		AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
		alertDialogFragment.setMessage(R.string.welcome_message_3);
		alertDialogFragment.setNumberOfButtons(1);
		alertDialogFragment.setPositiveButtonText(R.string.confirm);
		alertDialogFragment.setTargetFragment(this, 0);
		alertDialogFragment.setCancelable(false);
		alertDialogFragment.show(getFragmentManager(), "AlertDialogFragment");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.button_sign_up:
				uploadSignUpUserData();
				break;
		}
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		try {
			mSignUpFragmentCallback = (SignUpFragmentCallback) context;
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mSignUpFragmentCallback = null;
	}

	@Override
	public void onClickPositiveButton() {
		Intent intent = new Intent(getActivity(), MainActivity.class);
		startActivity(intent);
		getActivity().finish();
	}

	@Override
	public void onClickNegativeButton() {

	}

	@Override
	public void onClickSelectionButton(int index) {

	}

	public interface SignUpFragmentCallback {
		void onClickNavigationBackButton();

		void startProgressBar();

		void stopProgressBar();
	}
}
