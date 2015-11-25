package com.skku.se;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by XEiN on 11/10/15.
 */
public class AlertDialogFragment extends DialogFragment {

	public static final int SIMPLE_STYLE = 1;
	public static final int RADIO_BUTTON_STYLE = 2;

	private int mMessageResId;
	private int mNumberOfButtons = 1;
	private int mPositiveButtonTextResId;
	private int mNegativeButtonTextResId;
	private int mStyle = 1;
	private int mTitleResId;
	private int mSelectionStringArrayResId;

	private AlertDialogFragmentCallback mAlertDialogFragmentCallback;

	public void setMessage(int resId) {
		mMessageResId = resId;
	}

	public void setNumberOfButtons(int numberOfButtons) {
		mNumberOfButtons = numberOfButtons;
	}

	public void setPositiveButtonText(int resId) {
		mPositiveButtonTextResId = resId;
	}

	public void setNegativeButtonText(int resId) {
		mNegativeButtonTextResId = resId;
	}

	public void setDialogStyle(int style) {
		mStyle = style;
	}

	public void setTitle(int resId) {
		mTitleResId = resId;
	}

	public void setSelectionStringArray(int resId) {
		mSelectionStringArrayResId = resId;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getTargetFragment() != null) {
			mAlertDialogFragmentCallback = (AlertDialogFragmentCallback) getTargetFragment();
		}
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		if (mStyle == SIMPLE_STYLE) {
			if (mNumberOfButtons == 1) {
				builder.setMessage(mMessageResId).setPositiveButton(mPositiveButtonTextResId, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						mAlertDialogFragmentCallback.onClickPositiveButton();
					}
				});
			} else {
				builder.setMessage(mMessageResId).setPositiveButton(mPositiveButtonTextResId, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						mAlertDialogFragmentCallback.onClickPositiveButton();
					}
				}).setNegativeButton(mNegativeButtonTextResId, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						mAlertDialogFragmentCallback.onClickNegativeButton();
					}
				});

			}
		} else {
			builder.setTitle(mTitleResId).setItems(mSelectionStringArrayResId, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
						case 0:
							mAlertDialogFragmentCallback.onClickSelectionButton(0);
							break;
						case 1:
							mAlertDialogFragmentCallback.onClickSelectionButton(1);
							break;
					}
				}
			});
		}
		// Create the AlertDialog object and return it
		return builder.create();
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		try {
			mAlertDialogFragmentCallback = (AlertDialogFragmentCallback) context;
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
	}

	public interface AlertDialogFragmentCallback {
		void onClickPositiveButton();

		void onClickNegativeButton();

		void onClickSelectionButton(int index);
	}
}
