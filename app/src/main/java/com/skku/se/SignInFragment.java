package com.skku.se;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignInFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignInFragment extends Fragment implements View.OnClickListener {

	private EditText mUserIDEditText;
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
		mUserIDEditText = (EditText) rootView.findViewById(R.id.editText_id);
		mUserPasswordEditText = (EditText) rootView.findViewById(R.id.editText_password);
		mSignInButton = (Button) rootView.findViewById(R.id.button_sign_in);
		mSignUpButton = (Button) rootView.findViewById(R.id.button_go_sign_up_page);
	}

	private void setViewListeners() {
		mSignInButton.setOnClickListener(this);
		mSignUpButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.button_sign_in:
				Intent intent = new Intent(getActivity(), MainActivity.class);
				startActivity(intent);
				getActivity().finish();
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
	}
}
