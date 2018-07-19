package com.springboot.campspot.service;

import java.util.List;

import com.springboot.campspot.model.CampsiteRequest;

public interface ICampspotService {

	List<String> getAvailableCampsites(CampsiteRequest request, int gapDay);

}
