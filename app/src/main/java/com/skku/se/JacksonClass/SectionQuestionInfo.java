package com.skku.se.JacksonClass;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

/**
 * Created by XEiN on 12/4/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SectionQuestionInfo {
	public int question_id;

	public int chapter_id;

	public int level;

	public int section_id;

	public String question;

	public int answer_no;

	public ArrayList<Choices> choices;
}
