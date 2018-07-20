package com.springboot.campspot.service;

import java.util.Collection;

import com.springboot.campspot.model.CampsiteRequest;

public interface ICampspotService {

	Collection<String> getAvailableCampsites(CampsiteRequest request, int gapDay);

}
