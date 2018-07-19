package com.springboot.campspot.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.campspot.model.CampsiteRequest;
import com.springboot.campspot.service.impl.CampspotService;

@RestController
public class CampspotController {

	private static final Logger logger = LoggerFactory.getLogger(CampspotService.class);

	@Autowired
	CampspotService csService;

	@PostMapping("/getAvailableCamps")
	public List<String> getAvailableCampsites(@RequestBody CampsiteRequest request) {
		logger.info("hit controller, at /getAvailableCamps endpoint!");
		logger.info("calling campspotService.getAvailableCampsites()");
		int gapDay = 1;
		return csService.getAvailableCampsites(request, gapDay);
	}
}
