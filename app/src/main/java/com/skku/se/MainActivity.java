package com.skku.se;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.CircleProgress;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
	private static final String TAG = "MainActivity";

	private RelativeLayout mFileInputOutputRelativeLayout;
	private RelativeLayout mStructureRelativeLayout;
	private RelativeLayout mCompileFlowRelativeLayout;

	private TextView mFileInputOutputProgressTextView;
	private TextView mStructureProgressTextView;
	private TextView mCompileFlowProgressTextView;

	private CircleProgress mFileInputOutputCircleProgress;
	private CircleProgress mStructureCircleProgress;
	private CircleProgress mCompileFlowCircleProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
			}
		});

		configureLearningList();
	}

	private void configureLearningList() {
		mFileInputOutputRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout_file_input_output);
		mFileInputOutputRelativeLayout.setOnClickListener(this);

		mStructureRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout_structure);
		mStructureRelativeLayout.setOnClickListener(this);

		mCompileFlowRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout_compile_flow);
		mCompileFlowRelativeLayout.setOnClickListener(this);

		mFileInputOutputProgressTextView = (TextView) findViewById(R.id.textView_file_input_output_progress);
		mFileInputOutputProgressTextView.setText("");

		mStructureProgressTextView = (TextView) findViewById(R.id.textView_structure_progress);
		mStructureProgressTextView.setText("");

		mCompileFlowProgressTextView = (TextView) findViewById(R.id.textView_compile_flow_progress);
		mCompileFlowProgressTextView.setText("");

		mFileInputOutputCircleProgress = (CircleProgress) findViewById(R.id.circle_progress_file_input_output);
		mFileInputOutputCircleProgress.setMax(100);
		mFileInputOutputCircleProgress.setProgress(40);

		mStructureCircleProgress = (CircleProgress) findViewById(R.id.circle_progress_structure);
		mStructureCircleProgress.setMax(100);
		mStructureCircleProgress.setProgress(75);

		mCompileFlowCircleProgress = (CircleProgress) findViewById(R.id.circle_progress_compile_flow);
		mCompileFlowCircleProgress.setMax(100);
		mCompileFlowCircleProgress.setProgress(25);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(MainActivity.this, LearningActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
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
		// TODO sign out from the server
		callLoginActivity();
	}

	private void callLoginActivity() {
		Intent intent = new Intent(MainActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}
}
