package com.skku.se;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.navercorp.volleyextensions.volleyer.Volleyer;
import com.skku.se.JacksonClass.MainPageContents;
import com.skku.se.JacksonClass.QuestionList;

public class AdminActivity extends AppCompatActivity implements AlertDialogFragment.AlertDialogFragmentCallback {
	private static final String TAG = "AdminActivity";

	private ProgressBar mProgressBar;

	private RecyclerView mQuestionListRecyclerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		initProgressBar();
		downloadAdminContent();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, "onCreateOptionsMenu");
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
		clearSharedPreference();
		restartApplication();
	}

	private void clearSharedPreference() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		sharedPreferences.edit().clear().apply();
	}

	private void restartApplication() {
		Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		finish();
	}

	private void initProgressBar() {
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar_admin);
		mProgressBar.setVisibility(View.GONE);
	}

	private void downloadAdminContent() {
		startProgressBar();
		Volleyer.volleyer().get(getResources().getString(R.string.root_url) + "questions")
				.addHeader("Authorization", restoreSessionFromSharedPreferences()).withTargetClass(QuestionList.class)
				.withListener(new Response.Listener<QuestionList>() {
					@Override
					public void onResponse(QuestionList response) {
						stopProgressBar();
						configureQuestionList(response);
					}
				}).withErrorListener(new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				stopProgressBar();
				Toast.makeText(AdminActivity.this, "정보 가져오기를 실패하였습니다.", Toast.LENGTH_SHORT).show();
			}
		}).execute();
	}

	private String restoreSessionFromSharedPreferences() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		return String.valueOf(sharedPreferences.getInt(Preferences.SESSION_ID, -1));
	}

	private void configureQuestionList(QuestionList questionList) {
		mQuestionListRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_question_list);
		mQuestionListRecyclerView.setHasFixedSize(true);
		mQuestionListRecyclerView.setItemAnimator(new DefaultItemAnimator());

		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
		mQuestionListRecyclerView.setLayoutManager(linearLayoutManager);

		QuestionListAdapter questionListAdapter = new QuestionListAdapter(questionList.questions, getSupportFragmentManager());
		mQuestionListRecyclerView.setAdapter(questionListAdapter);
	}

	private void startProgressBar() {
		mProgressBar.setVisibility(View.VISIBLE);
	}

	private void stopProgressBar() {
		mProgressBar.setVisibility(View.GONE);
	}

	private void showApplicationTerminationDialog() {
		AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
		alertDialogFragment.setDialogStyle(AlertDialogFragment.SIMPLE_STYLE);
		alertDialogFragment.setMessage(R.string.are_you_sure_you_want_to_finish_the_app);
		alertDialogFragment.setPositiveButtonText(R.string.yes);
		alertDialogFragment.setNegativeButtonText(R.string.no);
		alertDialogFragment.setNumberOfButtons(2);
		alertDialogFragment.show(getSupportFragmentManager(), "application_termination_dialog");
	}

	@Override
	public void onBackPressed() {
		if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
			showApplicationTerminationDialog();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void onClickPositiveButton() {
		finish();
	}

	@Override
	public void onClickNegativeButton() {
		((AlertDialogFragment) getSupportFragmentManager().findFragmentByTag("application_termination_dialog")).dismiss();
	}

	@Override
	public void onClickSelectionButton(int index) {

	}
}
