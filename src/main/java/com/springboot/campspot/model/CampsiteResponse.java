package com.springboot.campspot.model;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CampsiteResponse {

	@JsonProperty
	private Collection<String> campsiteNames;

	public CampsiteResponse() {
		super();
	}

	public CampsiteResponse(Collection<String> campsiteNames) {
		super();
		this.campsiteNames = campsiteNames;
	}

	public Collection<String> getCampsiteNames() {
		return campsiteNames;
	}

	public void setCampsiteNames(Collection<String> campsiteNames) {
		this.campsiteNames = campsiteNames;
	}

}
