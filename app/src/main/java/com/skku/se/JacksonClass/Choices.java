package com.skku.se.JacksonClass;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by XEiN on 12/4/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Choices {
	public int choice_no;

	public String choice_detail;
}
