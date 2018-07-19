package com.springboot.campspot.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CampsiteRequest {

	@JsonProperty
	private SearchParameters search;
	
	@JsonProperty
	private List<Campsites> campsites;
	
	@JsonProperty
	private List<Reservations> reservations;

	public CampsiteRequest() {
		super();
	}
	
	public CampsiteRequest(SearchParameters search, List<Campsites> campsites, List<Reservations> reservations) {
		super();
		this.search = search;
		this.campsites = campsites;
		this.reservations = reservations;
	}

	public SearchParameters getSearch() {
		return search;
	}

	public void setSearch(SearchParameters search) {
		this.search = search;
	}

	public List<Campsites> getCampsites() {
		return campsites;
	}

	public void setCampsites(List<Campsites> campsites) {
		this.campsites = campsites;
	}

	public List<Reservations> getReservations() {
		return reservations;
	}

	public void setReservations(List<Reservations> reservations) {
		this.reservations = reservations;
	}
}
