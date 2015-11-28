package com.skku.se.JacksonClass;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by XEiN on 11/26/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AbstractSectionInfo {
	public int sectionId;

	public String sectionTitle;
}
