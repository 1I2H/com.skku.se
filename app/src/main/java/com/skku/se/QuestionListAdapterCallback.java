package com.skku.se;

/**
 * Created by XEiN on 12/3/15.
 */
public interface QuestionListAdapterCallback {
	void showAnswerDialog(String qnASectionName, String qnADetail, String qnADate, int questionId, int position);
}
