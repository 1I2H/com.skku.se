package com.skku.se.JacksonClass;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by XEiN on 11/26/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AbstractLevelInfo {
	public int chapter_id;

	public String level;

	public int current_section;

	public String section_title;

	public int progress_percentage;
}
