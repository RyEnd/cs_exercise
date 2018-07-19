package com.springboot.campspot.model;

public class Reservations {

	private long campsiteId;
	private String startDate;
	private String endDate;

	public Reservations() {

	}

	public Reservations(long campsiteId, String startDate, String endDate) {
		super();
		this.campsiteId = campsiteId;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public long getCampsiteId() {
		return campsiteId;
	}

	public void setCampsiteId(long campsiteId) {
		this.campsiteId = campsiteId;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	@Override
	public String toString() {
		return "\n\nReservations [campsiteId=" + campsiteId + ", startDate=" + startDate + ", endDate=" + endDate + "]\n\n";
	}

}
