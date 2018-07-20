package com.springboot.campspot.controller;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.campspot.model.CampsiteRequest;
import com.springboot.campspot.model.CampsiteResponse;
import com.springboot.campspot.service.impl.CampspotServiceImpl;

@RestController
public class CampspotController {

	private static final Logger logger = LoggerFactory.getLogger(CampspotServiceImpl.class);

	@Autowired
	CampspotServiceImpl csService;

	@PostMapping("/getAvailableCamps")
	public CampsiteResponse getAvailableCampsites(@RequestBody CampsiteRequest request) {
		logger.info("hit controller, at /getAvailableCamps endpoint!");
		logger.info("calling campspotService.getAvailableCampsites()");
		int gapDay = 1;
		Collection<String> listOfCampsiteNames = csService.getAvailableCampsites(request, gapDay);
		
		return new CampsiteResponse(listOfCampsiteNames);
	}
}
