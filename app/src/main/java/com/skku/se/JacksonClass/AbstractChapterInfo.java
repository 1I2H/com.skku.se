package com.skku.se.JacksonClass;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

/**
 * Created by XEiN on 11/26/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AbstractChapterInfo {
	public int chapter_id;

	public String chapter_title;

	public int current_level;

	public ArrayList<AbstractLevelInfo> progress_info;
}
