package com.skku.se.JacksonClass;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by XEiN on 11/27/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SectionContent {
	public int chapter_id;

	public String chapter_title;

	public int level;

	public int section_id;

	public String section_title;

	public String section_detail1;

	public String section_detail2;

	public String section_detail3;
}
