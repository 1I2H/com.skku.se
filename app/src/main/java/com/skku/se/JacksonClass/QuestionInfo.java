package com.skku.se.JacksonClass;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by XEiN on 12/3/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionInfo {
	public int question_id;

	public String section_title;

	public String question;

	public String question_date;

	public String answer;

	public String answer_date;
}
