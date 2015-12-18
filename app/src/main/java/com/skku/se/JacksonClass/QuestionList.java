package com.skku.se.JacksonClass;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

/**
 * Created by XEiN on 12/3/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionList {
	public ArrayList<QuestionInfo> questions;
}
