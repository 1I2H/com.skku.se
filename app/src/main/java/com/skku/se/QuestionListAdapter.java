package com.skku.se;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.skku.se.JacksonClass.QuestionInfo;

import java.util.ArrayList;

/**
 * Created by XEiN on 12/3/15.
 */
public class QuestionListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements QuestionListAdapterCallback,
		AnswerDialogFragment.AnswerDialogFragmentCallback {
	private static final String TAG = "QuestionListAdapter";

	private ArrayList<QuestionInfo> mQuestionInfoList;

	private FragmentManager mFragmentManager;

	public QuestionListAdapter(ArrayList<QuestionInfo> questionList, FragmentManager fragmentManager) {
		mQuestionInfoList = questionList;
		mFragmentManager = fragmentManager;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new QuestionViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_qna_row, parent, false));
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		QuestionInfo questionInfo = mQuestionInfoList.get(position);
		((QuestionViewHolder) holder).getSectionNameTextView().setText(questionInfo.section_title);
		((QuestionViewHolder) holder).getQnADetailTextView().setText(questionInfo.question);
		((QuestionViewHolder) holder).getDateTextView().setText(questionInfo.question_date);

		((QuestionViewHolder) holder).setQnASectionName(questionInfo.section_title);
		((QuestionViewHolder) holder).setQnADetail(questionInfo.question);
		((QuestionViewHolder) holder).setQnADate(questionInfo.question_date);
		((QuestionViewHolder) holder).setQuestionId(questionInfo.question_id);
		((QuestionViewHolder) holder).setPosition(position);
		((QuestionViewHolder) holder).setAdapterCallback(this);
	}

	@Override
	public int getItemCount() {
		return mQuestionInfoList.size();
	}

	public static class QuestionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		private View mQuestionView;
		private TextView mSectionNameTextView;
		private TextView mQnADetailTextView;
		private TextView mDateTextView;

		private String mQnASectionName;
		private String mQnADetail;
		private String mQnADate;
		private int mQuestionId;
		private int mPosition;

		private QuestionListAdapterCallback mQuestionListAdapterCallback;

		public QuestionViewHolder(View itemView) {
			super(itemView);
			mQuestionView = itemView;
			mSectionNameTextView = (TextView) itemView.findViewById(R.id.textView_section_name);
			mQnADetailTextView = (TextView) itemView.findViewById(R.id.textView_qna_detail);
			mDateTextView = (TextView) itemView.findViewById(R.id.textView_date);
		}

		public TextView getSectionNameTextView() {
			return mSectionNameTextView;
		}

		public TextView getQnADetailTextView() {
			return mQnADetailTextView;
		}

		public TextView getDateTextView() {
			return mDateTextView;
		}

		public void setQnASectionName(String qnASectionName) {
			mQnASectionName = qnASectionName;
		}

		public void setQnADetail(String qnADetail) {
			mQnADetail = qnADetail;
		}

		public void setQnADate(String qnADate) {
			mQnADate = qnADate;
		}

		public void setQuestionId(int questionId) {
			mQuestionId = questionId;
		}

		public void setPosition(int position) {
			mPosition = position;
		}

		public void setAdapterCallback(QuestionListAdapterCallback questionListAdapterCallback) {
			mQuestionListAdapterCallback = questionListAdapterCallback;
			mQuestionView.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			Log.d(TAG, "onClick");
			mQuestionListAdapterCallback.showAnswerDialog(mQnASectionName, mQnADetail, mQnADate, mQuestionId, mPosition);
		}
	}

	@Override
	public void showAnswerDialog(String qnASectionName, String qnADetail, String qnADate, int questionId, int position) {
		AnswerDialogFragment answerDialogFragment = AnswerDialogFragment.newInstance(qnASectionName, qnADetail, qnADate, questionId, position);
		answerDialogFragment.setAnswerDialogFragmentCallback(this);
		answerDialogFragment.show(mFragmentManager, null);
	}

	@Override
	public void onClickSendAnswer(int position) {
		mQuestionInfoList.remove(position);
		notifyItemRangeRemoved(position, 1);
	}
}
