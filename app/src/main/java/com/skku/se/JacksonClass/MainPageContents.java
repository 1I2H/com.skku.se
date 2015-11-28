package com.skku.se.JacksonClass;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by XEiN on 11/26/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MainPageContents {
	@JsonProperty("learningInfo")
	public ArrayList<AbstractChapterInfo> learningInfo;

	@JsonProperty("qnA")
	public ArrayList<QnAInfo> qnA;
}
