package com.springboot.campspot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.springboot.campspot.model.CampsiteRequest;
import com.springboot.campspot.model.Campsites;
import com.springboot.campspot.model.Reservations;
import com.springboot.campspot.model.SearchParameters;
import com.springboot.campspot.service.CampspotService;

@SpringBootTest
public class CampspotServiceMethodTests {

	@Test
	public void contextLoads() {
	}

	@Test
	public void testGetAvailableCampsites() {

		SearchParameters search = new SearchParameters("2018-06-04", "2018-06-06");
		Campsites cs1 = new Campsites(1, "Cozy Cabin");
		Campsites cs2 = new Campsites(2, "Comfy Cabin");
		Campsites cs3 = new Campsites(3, "Rustic Cabin");
		Campsites cs4 = new Campsites(4, "Rickety Cabin");
		Campsites cs5 = new Campsites(5, "Cabin in the Woods");
		List<Campsites> csList = new ArrayList<Campsites>() {
			{
				add(cs1);
				add(cs2);
				add(cs3);
				add(cs4);
				add(cs5);
			}
		};

		Reservations res1_1 = new Reservations(1, "2018-06-01", "2018-06-03");
		Reservations res1_2 = new Reservations(1, "2018-06-08", "2018-06-10");
		Reservations res2_1 = new Reservations(2, "2018-06-01", "2018-06-01");
		Reservations res2_2 = new Reservations(2, "2018-06-02", "2018-06-03");
		Reservations res2_3 = new Reservations(2, "2018-06-07", "2018-06-09");
		Reservations res3_1 = new Reservations(3, "2018-06-01", "2018-06-02");
		Reservations res3_2 = new Reservations(3, "2018-06-08", "2018-06-09");
		Reservations res4_1 = new Reservations(4, "2018-06-07", "2018-06-10");
		List<Reservations> resList = new ArrayList<Reservations>() {
			{
				add(res1_1);
				add(res1_2);
				add(res2_1);
				add(res2_2);
				add(res2_3);
				add(res3_1);
				add(res3_2);
				add(res4_1);
			}
		};

		CampsiteRequest request = new CampsiteRequest(search, csList, resList);

		CampspotService csSvc = new CampspotService();
		List<String> result = csSvc.getAvailableCampsites(request);

		assertEquals(2, result.size());
		assertTrue(result.contains("Rustic Cabin"));
		assertTrue(result.contains("Cabin in the Woods"));
		assertFalse(result.contains("Comfy Cabin"));
		assertFalse(result.contains("Cozy Cabin"));
		assertFalse(result.contains("Rickety Cabin"));

	}

}
