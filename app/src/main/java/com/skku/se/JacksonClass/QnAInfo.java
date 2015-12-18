package com.skku.se.JacksonClass;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by XEiN on 11/26/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class QnAInfo {
	public int question_id;

	public String section_title;

	public String question;

	public String question_date;

	public String answer;

	public String answer_date;
}
