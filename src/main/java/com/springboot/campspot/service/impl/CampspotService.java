package com.springboot.campspot.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import com.springboot.campspot.service.ICampspotService;

@Service
public class CampspotService implements ICampspotService {
	private static final Logger logger = LoggerFactory.getLogger(CampspotService.class);
	private static final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");

	@Override
	public List<String> getAvailableCampsites(CampsiteRequest request, int gapDay) {
		logger.info("at the start of getAvailableCampsites()");

		// filter out campsites who meets the gap day logic.
		List<String> availableCampsites = filterCampsitesWithGap(request, gapDay);

		return availableCampsites;
	}

	private List<String> filterCampsitesWithGap(CampsiteRequest request, int gapDay) {
		Set<Long> availableCampIds = request.getCampsites().stream().map(c -> c.getId()).collect(Collectors.toSet());

		// group existing reservations by campsiteId
		Collection<List<Reservations>> existingReservations = request.getReservations().stream()
				.collect(Collectors.groupingBy(res -> res.getCampsiteId())).values();

		// loop through each reservation and pull out the dates
		// then compare against searchStartDate and searchEndDate
		for (List<Reservations> resList : existingReservations) {

			// add requested start and end date to resList
			addRequestedDatesToReservationGroups(request, resList);

			// flatten start and end date of all reservation into a list of dates
			List<DateTime> resDateList = sortAndSliceReservationDateList(request, resList);

			// add 06/02 and 06/08 to a blacklist
			Set<DateTime> blackListedDates = populateBlackListedDates(request, gapDay);

			filterForAvailableCampsites(availableCampIds, resList, resDateList, blackListedDates);
		}

		return getAvailableCampsiteNames(request, availableCampIds);

	}

	// this needs to change to accomodate for 2+ gap day rules.
	private void filterForAvailableCampsites(Set<Long> availableCampIds, List<Reservations> resList,
			List<DateTime> resDateList, Set<DateTime> blackListedDates) {
		// if a reservation triggers the gap day rule
		// then the campsiteId is filtered out from allCampIds
		// currently doesn't have the flexibility to deal with 2+ day gap rules.
		for (DateTime dt : blackListedDates) {
			if (resDateList.contains(dt)) {
				availableCampIds.remove(resList.get(0).getCampsiteId());
			}
		}
	}

	private List<DateTime> sortAndSliceReservationDateList(CampsiteRequest request, List<Reservations> resList) {
		List<DateTime> resDateList = resList.stream().flatMap(
				r -> Stream.of(formatter.parseDateTime(r.getStartDate()), formatter.parseDateTime(r.getEndDate())))
				.collect(Collectors.toList());

		Collections.sort(resDateList);

		int startDateIndex = resDateList.indexOf(formatter.parseDateTime(request.getSearch().getStartDate()));
		int endDateIndex = resDateList.indexOf(formatter.parseDateTime(request.getSearch().getEndDate()));

		if (startDateIndex == 0) {
			resDateList = resDateList.subList(0, 3);
		} else if (endDateIndex == resDateList.size() - 1) {
			resDateList = resDateList.subList(resDateList.size() - 3, resDateList.size());
		} else {
			resDateList = resDateList.subList(startDateIndex - 1, endDateIndex + 2);
		}

		logger.info("LIST!!!!!");
		logger.info(resDateList.toString());
		return resDateList;
	}

	private void addRequestedDatesToReservationGroups(CampsiteRequest request, List<Reservations> resList) {
		resList.add(new Reservations(resList.get(0).getCampsiteId(), request.getSearch().getStartDate(),
				request.getSearch().getEndDate()));
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

	private Set<DateTime> populateBlackListedDates(CampsiteRequest request, int gapDay) {
		int effectiveGapDay = gapDay + 1;
		Set<DateTime> blackListedDates = new HashSet<>();

		DateTime twoDaysBeforeStartDate = formatter.parseDateTime(request.getSearch().getStartDate());
		twoDaysBeforeStartDate = twoDaysBeforeStartDate.minusDays(effectiveGapDay);
		blackListedDates.add(twoDaysBeforeStartDate);

		DateTime twoDaysAfterEndDate = formatter.parseDateTime(request.getSearch().getEndDate());
		twoDaysAfterEndDate = twoDaysAfterEndDate.plusDays(effectiveGapDay);
		blackListedDates.add(twoDaysAfterEndDate);

		return blackListedDates;
	}

}
