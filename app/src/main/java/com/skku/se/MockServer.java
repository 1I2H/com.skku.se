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

	public void makeLearningContentsList() {
		try {
			mLearningContentsList = new JSONArray();

			for (int i = 0; i < 10; i++) {
				JSONObject learningContents = new JSONObject();
				learningContents.put("pageType", 1);
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
}
