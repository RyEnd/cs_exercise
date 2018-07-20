package com.springboot.campspot.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;

import com.springboot.campspot.model.CampsiteRequest;
import com.springboot.campspot.model.Campsites;
import com.springboot.campspot.model.Reservations;
import com.springboot.campspot.service.ICampspotService;

@Service
public class CampspotServiceImpl implements ICampspotService {
	private static final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");

	@Override
	public Collection<String> getAvailableCampsites(CampsiteRequest request, int gapDay) {

		/*
		 * -- OVERVIEW OF APPROACH --
		 * 
		 * in filterCampsitesWithGap method, i approached the problem in the following
		 * way:
		 * 
		 * 1. converted the list of all available campsites (with ID and name) into a map 
		 * 2. grouped the list of all existing reservations into a collection of list, grouped by campsite id 
		 * 3a. enter the loop that iterates through the collection of lists 
		 * 3b. add requested dates to each list, to 'pretend' that the requested dates were booked 
		 * 3c. extract out the start / end dates for each list and convert to DateTime lists, then sort them 
		 * 4. create a list of "blacklisted dates", dates that cannot exist in the list in step 3c 
		 * 5. filter the list in step 3c against the blacklist in step 4 in order to remove disqualified campsites from map in step 1 
		 * 6. exit the loop from step 3a 
		 * 7. grab the map.values() which returns a collection of campsite names
		 * 
		 * -- ASSUMPTIONS I MADE -- 
		 * 
		 * 1. i made the assumption that, if a campsite appears on the list of campsites in the request 
		 * but does not appear in the list of reservations, then he campsite was not booked at all 
		 * and was available for booking
		 * 
		 * 2. one day gap rule, gapDay value of 1 is hard coded and passed from the
		 * controller.
		 */
		Collection<String> availableCampsites = filterCampsitesWithGap(request, gapDay);

		return availableCampsites;
	}

	private Collection<String> filterCampsitesWithGap(CampsiteRequest request, int gapDay) {

		// tease out a map of campsite id as key, and campsite name as value.
		Map<Long, String> campIdAndNameHashmap = request.getCampsites().stream()
				.collect(Collectors.toMap(Campsites::getId, Campsites::getName));

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

			// add the 1 day gap rule violating dates to a blacklist
			Set<DateTime> blackListedDates = populateBlackListedDates(request, gapDay);

			filterForAvailableCampsites(campIdAndNameHashmap, resList, resDateList, blackListedDates);
		}

		return campIdAndNameHashmap.values();

	}

	private void filterForAvailableCampsites(Map<Long, String> campIdAndNameHashmap, List<Reservations> resList,
			List<DateTime> resDateList, Set<DateTime> blackListedDates) {
		
		// if a reservation triggers the gap day rule
		// then the campsiteId is filtered out from campIdAndNameHashmap
		// currently doesn't have the flexibility to deal with 2+ day gap rules.
		
		for (DateTime dt : blackListedDates) {
			if (resDateList.contains(dt)) {
				campIdAndNameHashmap.remove(resList.get(0).getCampsiteId());
			}
		}
	}

	private List<DateTime> sortAndSliceReservationDateList(CampsiteRequest request, List<Reservations> resList) {

		// create a list of DateTime objects (including requested start and end dates)
		List<DateTime> resDateList = resList.stream().flatMap(
				r -> Stream.of(formatter.parseDateTime(r.getStartDate()), formatter.parseDateTime(r.getEndDate())))
				.collect(Collectors.toList());

		// sort the dates in ascending order
		Collections.sort(resDateList);

		// find where the requested start and end dates are on the list
		int startDateIndex = resDateList.indexOf(formatter.parseDateTime(request.getSearch().getStartDate()));
		int endDateIndex = resDateList.indexOf(formatter.parseDateTime(request.getSearch().getEndDate()));

		if (startDateIndex == 0) {

			// if there were no existing reservations before requested start date
			// then the requested start date is at index of 0.
			// slice the list to include requested start date, requested end date, and the
			// very next date.

			resDateList = resDateList.subList(0, 3);

		} else if (endDateIndex == resDateList.size() - 1) {

			// if there were no existing reservations after the requested end date
			// then the requested end date is at the last index
			// slice the list to include the value before the requested start date,
			// as well as the requested start date and requested end date.

			resDateList = resDateList.subList(resDateList.size() - 3, resDateList.size());

		} else {
			
			// every other scenario falls into this else block
			// grab the value before the requested start date, then the requested start /
			// end date
			// then the value after the requested end date.
			
			resDateList = resDateList.subList(startDateIndex - 1, endDateIndex + 2);
		}

		return resDateList;
	}

	private void addRequestedDatesToReservationGroups(CampsiteRequest request, List<Reservations> resList) {

		// add the requested start and end dates to the list of Reservations
		resList.add(new Reservations(resList.get(0).getCampsiteId(), request.getSearch().getStartDate(),
				request.getSearch().getEndDate()));
	}

	private Set<DateTime> populateBlackListedDates(CampsiteRequest request, int gapDay) {
		
		// for the test case, the days that should not exist on the list of existing reservation start / end dates
		// are 6/2 and 6/8
		// because date range of 6/2 - 6/4 and 6/6 - 6/8 represents a 1 day gap
		// so i am adding 1 more day to the gap day given
		// and subtracting / adding that number of days (2) to the requested start and end date, respectively
		// to create the list of dates i should not see in the list of existing reservation dates.
		
		int effectiveGapDay = gapDay + 1;
		Set<DateTime> blackListedDates = new HashSet<>();

		DateTime twoDaysBeforeStartDate = formatter.parseDateTime(request.getSearch().getStartDate()).minusDays(effectiveGapDay);
		blackListedDates.add(twoDaysBeforeStartDate);

		DateTime twoDaysAfterEndDate = formatter.parseDateTime(request.getSearch().getEndDate()).plusDays(effectiveGapDay);
		blackListedDates.add(twoDaysAfterEndDate);

		return blackListedDates;
	}

}