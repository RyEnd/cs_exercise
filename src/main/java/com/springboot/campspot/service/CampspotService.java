package com.springboot.campspot.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.springboot.campspot.model.CampsiteRequest;
import com.springboot.campspot.model.Reservations;

@Service
public class CampspotService {
	private static final Logger logger = LoggerFactory.getLogger(CampspotService.class);
	private static final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");

	public List<String> getAvailableCampsites(CampsiteRequest request) {
		logger.info("at the start of getAvailableCampsites()");

		// filter out campsites who meets the gap day logic.
		List<String> availableCampsites = filterCampsitesWithGap(request);

		return availableCampsites;
	}

	private List<String> filterCampsitesWithGap(CampsiteRequest request) {
		Set<Long> availableCampIds = request.getCampsites().stream().map(c -> c.getId()).collect(Collectors.toSet());

		// group existing reservations by campsiteId
		Collection<List<Reservations>> existingReservations = request.getReservations().stream()
				.collect(Collectors.groupingBy(res -> res.getCampsiteId())).values();

		// loop through each reservation and pull out the dates
		// then compare against searchStartDate and searchEndDate
		for (List<Reservations> res : existingReservations) {

			// flatten start and end date of all reservation per camp site id into a list of
			// dates
			Set<DateTime> realDates = res.stream().flatMap(
					r -> Stream.of(formatter.parseDateTime(r.getStartDate()), formatter.parseDateTime(r.getEndDate())))
					.collect(Collectors.toSet());

			// add 06/03 and 06/07 (1 day less than 6/4 and 1 day more than 6/6) to a list
			Set<DateTime> blackListedDates = populateBlackListedDates(request);

			// if a reservation triggers the 1 day gap rule
			// then the campsiteId is filtered out from allCampIds
			// currently doesn't have the flexibility to deal with 2+ day gap rules.
			for (DateTime dt : blackListedDates) {
				if (realDates.contains(dt)) {
					availableCampIds.remove(res.get(0).getCampsiteId());
				}
			}
		}

		return getAvailableCampsiteNames(request, availableCampIds);

	}

	private List<String> getAvailableCampsiteNames(CampsiteRequest request, Set<Long> availableCampIds) {
		List<String> availableCampsiteNames = new ArrayList<>();
		// loop through the filtered availableCampIds populate the name list
		for (Long id : availableCampIds) {
			String campSiteName = request.getCampsites().stream().filter(c -> c.getId() == id).findFirst().get()
					.getName();
			availableCampsiteNames.add(campSiteName);
		}

		return availableCampsiteNames;
	}

	private Set<DateTime> populateBlackListedDates(CampsiteRequest request) {
		Set<DateTime> blackListedDates = new HashSet<>();

		DateTime oneDayBeforeStartDate = formatter.parseDateTime(request.getSearch().getStartDate());
		oneDayBeforeStartDate = oneDayBeforeStartDate.minusDays(1);
		blackListedDates.add(oneDayBeforeStartDate);

		DateTime oneDayAfterEndDate = formatter.parseDateTime(request.getSearch().getEndDate());
		oneDayAfterEndDate = oneDayAfterEndDate.plusDays(1);
		blackListedDates.add(oneDayAfterEndDate);

		return blackListedDates;
	}

}
