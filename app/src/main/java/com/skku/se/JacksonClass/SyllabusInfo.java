package com.skku.se.JacksonClass;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

/**
 * Created by XEiN on 11/26/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SyllabusInfo {
	public int chapter_id;

	public String chapter_title;

	public ArrayList<AbstractSectionInfo> sections;
}
