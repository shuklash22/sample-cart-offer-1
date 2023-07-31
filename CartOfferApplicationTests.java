package com.springboot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.controller.OfferRequest;
import com.springboot.controller.SegmentResponse;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CartOfferApplicationTests {


	@Test
	public void checkFlatXForOneSegment() throws Exception {
		List<String> segments = new ArrayList<>();
		segments.add("p1");
		OfferRequest offerRequest = new OfferRequest(1,"FLATX",10,segments);
		boolean result = addOffer(offerRequest);
		Assert.assertEquals(result,true); // able to add offer
	}

	public boolean addOffer(OfferRequest offerRequest) throws Exception {
		String urlString = "http://localhost:9001/api/v1/offer";
		URL url = new URL(urlString);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setDoOutput(true);
		con.setRequestProperty("Content-Type", "application/json");

		ObjectMapper mapper = new ObjectMapper();

		String POST_PARAMS = mapper.writeValueAsString(offerRequest);
		OutputStream os = con.getOutputStream();
		os.write(POST_PARAMS.getBytes());
		os.flush();
		os.close();
		int responseCode = con.getResponseCode();
		System.out.println("POST Response Code :: " + responseCode);

		if (responseCode == HttpURLConnection.HTTP_OK) { //success
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			// print result
			System.out.println(response.toString());
		} else {
			System.out.println("POST request did not work.");
		}
		return true;
	}

	@Test
	public void checkFlatXForMultipleSegment() throws Exception {
		List<String> segments = new ArrayList<>();
		segments.add("p1");
		segments.add("p2");
		segments.add("p3");
		OfferRequest offerRequest = new OfferRequest(1, "FLATX", 10, segments);
		boolean result = addOffer(offerRequest);
		Assert.assertTrue(result); // Offer added successfully

		// Create a cart with a specific cart_value
		int cartValue = 200; // Change this value to the desired cart value

		// Apply the offers to the cart
		String urlString = "http://localhost:9001/api/v1/cart/apply_offer";
		URL url = new URL(urlString);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setDoOutput(true);
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");

		ObjectMapper mapper = new ObjectMapper();
		String jsonRequest = "{\"cart_value\":" + cartValue + ",\"user_id\":1,\"restaurant_id\":1}";
		OutputStream os = con.getOutputStream();
		os.write(jsonRequest.getBytes());
		os.flush();
		os.close();

		int responseCode = con.getResponseCode();
		Assert.assertEquals(HttpURLConnection.HTTP_OK, responseCode); // Request successful

		// Read the response
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		StringBuffer response = new StringBuffer();
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// Parse the response to get the updated cart_value
		int updatedCartValue = Integer.parseInt(response.toString());

		// Verify that the cart value is reduced based on the applicable offers for the specific customer segments
		// Calculate the expected cart value based on the applied offers (e.g., Flat 10% off for p1, p2, and p3)
		int expectedCartValue = (int) (cartValue * (1 - (10.0 / 100)));
		Assert.assertEquals(expectedCartValue, updatedCartValue);
	}

	@Test
	public void applyFlatXAmountOffOffer() throws Exception {
		List<String> segments = new ArrayList<>();
		segments.add("p1");
		OfferRequest offerRequest = new OfferRequest(1, "FLATX", 10, segments);
		boolean offerAdded = addOffer(offerRequest);
		Assert.assertTrue(offerAdded); // Offer added successfully

		// Create a cart with a specific cart_value
		int cartValue = 200; // Change this value to the desired cart value
		int expectedCartValue = cartValue - 10; // offer_value is 10

		// Apply the offer to the cart
		String urlString = "http://localhost:9001/api/v1/cart/apply_offer";
		URL url = new URL(urlString);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setDoOutput(true);
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");

		ObjectMapper mapper = new ObjectMapper();
		String jsonRequest = "{\"cart_value\":" + cartValue + ",\"user_id\":1,\"restaurant_id\":1}";
		OutputStream os = con.getOutputStream();
		os.write(jsonRequest.getBytes());
		os.flush();
		os.close();

		int responseCode = con.getResponseCode();
		Assert.assertEquals(HttpURLConnection.HTTP_OK, responseCode); // Request successful

		// Read the response
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		StringBuffer response = new StringBuffer();
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// Parse the response to get the updated cart_value
		int updatedCartValue = Integer.parseInt(response.toString());
		Assert.assertEquals(expectedCartValue, updatedCartValue); // Cart value reduced as expected
	}
	@Test
	public void applyFlatXPercentageOffOffer() throws Exception {
		List<String> segments = new ArrayList<>();
		segments.add("p1");
		OfferRequest offerRequest = new OfferRequest(1, "FLATX%", 10, segments);
		boolean offerAdded = addOffer(offerRequest);
		Assert.assertTrue(offerAdded); // Offer added successfully

		// Create a cart with a specific cart_value
		int cartValue = 200; // Change this value to the desired cart value
		int expectedCartValue = (int) (cartValue * (1 - (10 / 100.0))); // offer_value is 10 (percentage)

		// Apply the offer to the cart
		String urlString = "http://localhost:9001/api/v1/cart/apply_offer";
		URL url = new URL(urlString);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setDoOutput(true);
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");

		ObjectMapper mapper = new ObjectMapper();
		String jsonRequest = "{\"cart_value\":" + cartValue + ",\"user_id\":1,\"restaurant_id\":1}";
		OutputStream os = con.getOutputStream();
		os.write(jsonRequest.getBytes());
		os.flush();
		os.close();

		int responseCode = con.getResponseCode();
		Assert.assertEquals(HttpURLConnection.HTTP_OK, responseCode); // Request successful

		// Read the response
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		StringBuffer response = new StringBuffer();
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// Parse the response to get the updated cart_value
		int updatedCartValue = Integer.parseInt(response.toString());
		Assert.assertEquals(expectedCartValue, updatedCartValue); // Cart value reduced as expected
	}
	@Test
	public void applyInvalidOffer() throws Exception {
		List<String> segments = new ArrayList<>();
		segments.add("p1");
		// Assuming the offer with ID 100 is not available for segment "p1"
		OfferRequest invalidOfferRequest = new OfferRequest(100, "FLATX", 10, segments);
		boolean offerAdded = addOffer(invalidOfferRequest);
		Assert.assertFalse(offerAdded); // Offer should not be added successfully

		// Create a cart with a specific cart_value
		int cartValue = 200; // Change this value to the desired cart value

		// Apply the invalid offer to the cart
		String urlString = "http://localhost:9001/api/v1/cart/apply_offer";
		URL url = new URL(urlString);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setDoOutput(true);
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");

		ObjectMapper mapper = new ObjectMapper();
		String jsonRequest = "{\"cart_value\":" + cartValue + ",\"user_id\":1,\"restaurant_id\":100}";
		OutputStream os = con.getOutputStream();
		os.write(jsonRequest.getBytes());
		os.flush();
		os.close();

		int responseCode = con.getResponseCode();
		Assert.assertNotEquals(HttpURLConnection.HTTP_OK, responseCode); // Request should not be successful

		// Read the error response
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
		StringBuffer response = new StringBuffer();
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// Verify that the error message indicates the offer is not valid or not available
		String errorMessage = response.toString();
		Assert.assertTrue(errorMessage.contains("error") || errorMessage.contains("not valid") || errorMessage.contains("not available"));
	}

	@Test
	public void noOffersAppliedToCart() throws Exception {

		// Create a cart with a specific cart_value
		int cartValue = 200; // Change this value to the desired cart value

		// Apply no offers (empty offers list) to the cart
		String urlString = "http://localhost:9001/api/v1/cart/apply_offer";
		URL url = new URL(urlString);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setDoOutput(true);
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");

		ObjectMapper mapper = new ObjectMapper();
		String jsonRequest = "{\"cart_value\":" + cartValue + ",\"user_id\":1,\"restaurant_id\":1}";
		OutputStream os = con.getOutputStream();
		os.write(jsonRequest.getBytes());
		os.flush();
		os.close();

		int responseCode = con.getResponseCode();
		Assert.assertEquals(HttpURLConnection.HTTP_OK, responseCode); // Request successful

		// Read the  response
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
		StringBuffer response = new StringBuffer();
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// Parse the response to get the updated cart_value
		int updatedCartValue = Integer.parseInt(response.toString());

		// Verify that the cart value remains unchanged
		Assert.assertEquals(cartValue, updatedCartValue);
	}

	@Test
	public void NegativeCartValue() throws Exception {
		List<String> segments = new ArrayList<>();
		segments.add("p1");
		OfferRequest offerRequest = new OfferRequest(1, "FLATX%", 10, segments);
		boolean offerAdded = addOffer(offerRequest);
		Assert.assertTrue(offerAdded); // Offer added successfully

		// Create a cart with a specific cart_value
		int cartValue = -100;

		// Apply the offer to the cart
		String urlString = "http://localhost:9001/api/v1/cart/apply_offer";
		URL url = new URL(urlString);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setDoOutput(true);
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");

		ObjectMapper mapper = new ObjectMapper();
		String jsonRequest = "{\"cart_value\":" + cartValue + ",\"user_id\":1,\"restaurant_id\":1}";
		OutputStream os = con.getOutputStream();
		os.write(jsonRequest.getBytes());
		os.flush();
		os.close();

		int responseCode = con.getResponseCode();
		Assert.assertEquals(HttpURLConnection.HTTP_OK, responseCode); // Request successful

		// Read the response
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		StringBuffer response = new StringBuffer();
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// Parse the response to get the updated cart_value
		int updatedCartValue = Integer.parseInt(response.toString());

		// verify that the rsulting cart value is still neagtive after applying the offer
		Assert.assertTrue(updatedCartValue < 0);
	}









