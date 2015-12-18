package com.skku.se;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by XEiN on 11/7/15.
 */
public class MockServer {
	private static MockServer mMockServer;

	private Context mContext;

	private JSONObject mOverAllContents;
	private JSONArray mLearningContentsList;

	private MockServer(Context context) {
		mContext = context;
	}

	public static MockServer getInstance(Context context) {
		if (mMockServer == null) {
			mMockServer = new MockServer(context);
		}
		return mMockServer;
	}

	public MockServer makeOverallContents() {
		try {
			mOverAllContents = new JSONObject();
			JSONArray learningInfo = new JSONArray();

			for (int i = 0; i < 3; i++) {
				if (i == 0) {
					learningInfo.put(makeLearningChapter("파일 입/출력", 1, "파일 입/출력 기본", 4, 1));
				} else if (i == 1) {
					learningInfo.put(makeLearningChapter("구조체", 2, "구조체 포인터", 2, 1));
				} else {
					learningInfo.put(makeLearningChapter("컴파일 과정", 1, "컴파일 과정 기본", 3, 1));
				}
			}

			mOverAllContents.put("learning_info", learningInfo);
			mOverAllContents.put("qna", makeQnAList());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return this;
	}

	private JSONObject makeLearningChapter(String title, int level, String progressDetail, int sectionCount, int currentSection) throws
			JSONException {
		JSONObject learningChapterObject = new JSONObject();
		learningChapterObject.put("title", title);
		learningChapterObject.put("level", level);
		learningChapterObject.put("beginnerProgressDetail", progressDetail);
		learningChapterObject.put("intermediateProgressDetail", progressDetail + " intermediate");
		learningChapterObject.put("beginnerSectionCount", sectionCount);
		learningChapterObject.put("intermediateSectionCount", sectionCount + 1);
		learningChapterObject.put("beginnerCurrentSection", currentSection);
		learningChapterObject.put("intermediateCurrentSection", currentSection + 1);

		return learningChapterObject;
	}

	private JSONArray makeQnAList() throws JSONException {
		JSONArray qnAList = new JSONArray();
		for (int i = 0; i < 10; i++) {
			qnAList.put(makeQnA());
		}

		return qnAList;
	}

	private JSONObject makeQnA() throws JSONException {
		JSONObject qnAObject = new JSONObject();
		qnAObject.put("section_title", "구조체");
		qnAObject.put("question", "구조체 포인터에 관한 질분입니다.");
		qnAObject.put("question_date", "15/11/07");
		qnAObject.put("answer", "답변입니다.");
		qnAObject.put("answer_date", "15/11/07");

		return qnAObject;
	}

	public void makeLearningContentsList() {
		try {
			mLearningContentsList = new JSONArray();

			for (int i = 0; i < 3; i++) {
				JSONObject learningContents = new JSONObject();
				learningContents.put("pageType", i + 1);
				learningContents.put("pageInfo", "학습페이지입니다.");
				learningContents.put("sectionNumber", 1);
				learningContents.put("subSectionNumber", i + 1);
				mLearningContentsList.put(learningContents);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public JSONArray getLearningContentsList() {
		return mLearningContentsList;
	}

	public JSONObject getOverAllContents() {
		return mOverAllContents;
	}
}
