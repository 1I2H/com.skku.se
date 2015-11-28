package com.skku.se.JacksonClass;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by XEiN on 11/26/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AbstractLevelInfo {
	public int level;

	public int progressSection;

	public String progressDetail;

	public int progressPercentage;
}
