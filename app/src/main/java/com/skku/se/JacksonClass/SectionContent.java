package com.skku.se.JacksonClass;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by XEiN on 11/27/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SectionContent {
	public int chapterId;

	public String chapterTitle;

	public int level;

	public int sectionId;

	public String sectionTitle;
}
