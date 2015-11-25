package com.skku.se;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpFragment extends Fragment implements View.OnClickListener, AlertDialogFragment.AlertDialogFragmentCallback {
	private static final String TAG = "SignUpFragment";

	private Toolbar mToolbar;
	private EditText mSignUpIdEditText;
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
		mSignUpIdEditText = (EditText) rootView.findViewById(R.id.editText_id_sign_up);
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
				showWelcomeDialog();
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
		// do login
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
	}
}
