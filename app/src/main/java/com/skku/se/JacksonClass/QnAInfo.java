package com.skku.se.JacksonClass;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by XEiN on 11/26/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class QnAInfo {
	public int questionId;

	public String sectionName;

	public String question;

	public String questionDate;

	public String answer;

	public String answerDate;
}
