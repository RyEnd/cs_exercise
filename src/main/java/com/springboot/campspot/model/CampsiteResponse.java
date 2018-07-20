package com.springboot.campspot.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CampsiteResponse {

	@JsonProperty
	private List<String> campsiteNames;

	public CampsiteResponse() {
		super();
	}

	public CampsiteResponse(List<String> campsiteNames) {
		super();
		this.campsiteNames = campsiteNames;
	}

	public List<String> getCampsiteNames() {
		return campsiteNames;
	}

	public void setCampsiteNames(List<String> campsiteNames) {
		this.campsiteNames = campsiteNames;
	}

}
