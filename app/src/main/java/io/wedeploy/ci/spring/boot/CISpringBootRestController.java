package io.wedeploy.ci.spring.boot;

import com.commsen.wedeploy.client.WeDeployClient;
import com.commsen.wedeploy.client.WeDeployClientException;
import com.commsen.wedeploy.client.data.CollectionDTO;
import com.commsen.wedeploy.client.data.WeDeployDataCollection;
import com.commsen.wedeploy.client.data.WeDeployDataDocument;
import com.commsen.wedeploy.client.data.WeDeployDataService;
import com.commsen.wedeploy.client.data.WeDeployDataStorage;

import com.wedeploy.android.WeDeploy;
import com.wedeploy.android.WeDeploy.Builder;
import com.wedeploy.android.exception.WeDeployException;
import com.wedeploy.android.query.Query;
import com.wedeploy.android.query.SortOrder;
import com.wedeploy.android.query.filter.Filter;
import com.wedeploy.android.transport.Response;

import io.wedeploy.ci.jenkins.node.JenkinsMasters;
import io.wedeploy.ci.jenkins.node.JenkinsMastersImpl;
import io.wedeploy.ci.util.EnvironmentUtil;

import java.io.IOException;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class CISpringBootRestController {

	@GetMapping("/masters")
	public String masters() throws IOException {
		JenkinsMasters jenkinsMasters = new JenkinsMastersImpl();

		return jenkinsMasters.toString();
	}

	@GetMapping("/counts")
	public String counts() throws IOException {
		JenkinsMasters jm = new JenkinsMastersImpl();

		return jm.getOverviewInformation();
	}

	@GetMapping("/api")
	public String restAPI() throws WeDeployClientException {
		WeDeployClient weDeploy = new WeDeployClient();

		WeDeployDataService weDeployDataService = weDeploy.data();

		WeDeployDataStorage weDeployDataStorage = weDeployDataService.connect(
			"ci", "data");

		CollectionDTO moviesCollectionDTO = CollectionDTO.from("movies");

		if (!weDeployDataStorage.collectionExists("movies")) {
			weDeployDataStorage.createCollection(moviesCollectionDTO);

			WeDeployDataCollection weDeployDataCollection =
				weDeployDataStorage.collection("movies");

			JSONObject jsonObject = new JSONObject();

			jsonObject.put("title", "starwars");
			jsonObject.put("rating", 9.8);

			WeDeployDataDocument<JSONObject> weDeployDataDocument =
				new WeDeployDataDocument<JSONObject>("movie-1", jsonObject);

			System.out.println(weDeployDataDocument.getId());
			System.out.println(weDeployDataDocument.getObject());

			weDeployDataCollection.save(weDeployDataDocument);

			return "Created a 'movies' collection";
		}

		weDeployDataStorage.deleteCollections(moviesCollectionDTO);

		return "Deleted a 'movies' collection";
	}

	@GetMapping("/delete")
	public String delete() throws WeDeployException {
		WeDeploy weDeploy = new WeDeploy.Builder().build();

		Response response = weDeploy
			.data("https://data-ci.wedeploy.io")
			.delete("movies")
			.execute();

		return response.getBody();
	}

	@GetMapping("/read")
	public String read() throws WeDeployException {
		WeDeploy weDeploy = new WeDeploy.Builder().build();

		Response response = weDeploy
			.data("https://data-ci.wedeploy.io")
			.orderBy("rating", SortOrder.DESCENDING)
			.get("movies")
			.execute();

		return response.getBody();
	}

	@GetMapping("/write")
	public String write() throws WeDeployException {
		WeDeploy weDeploy = new WeDeploy.Builder().build();

		JSONObject movie1JsonObject = new JSONObject()
			.put("title", "Star Wars III")
			.put("year", 2005)
			.put("rating", 8.0);

		JSONObject movie2JsonObject = new JSONObject()
			.put("title", "Star Wars II")
			.put("year", 2002)
			.put("rating", 8.6);

		JSONArray moviesJsonArray = new JSONArray()
			.put(movie1JsonObject)
			.put(movie2JsonObject);

		Response response = weDeploy
			.data("https://data-ci.wedeploy.io")
			.create("movies", moviesJsonArray)
			.execute();

		return response.getBody();
	}

	@GetMapping("/api2")
	public String quick() throws WeDeployException {
		WeDeploy weDeploy = new WeDeploy.Builder().build();

		/* Adding data */

		JSONObject movie1JsonObject = new JSONObject()
			.put("title", "Star Wars III")
			.put("year", 2005)
			.put("rating", 8.0);

		JSONObject movie2JsonObject = new JSONObject()
			.put("title", "Star Wars II")
			.put("year", 2002)
			.put("rating", 8.6);

		JSONArray moviesJsonArray = new JSONArray()
			.put(movie1JsonObject)
			.put(movie2JsonObject);

		Response response = weDeploy
			.data("https://data-ci.wedeploy.io")
			.create("movies", moviesJsonArray)
			.execute();

		System.out.println(response.getBody());

		/* Retrieving data */

		response = weDeploy
			.data("https://data-ci.wedeploy.io")
			.get("movies")
			.execute();

		System.out.println(response.getBody());

		JSONArray jsonArray = new JSONArray(response.getBody());

		System.out.println(jsonArray.get(0));
		System.out.println(jsonArray.get(1));

		/* Deleting data */

		response = weDeploy
			.data("https://data-ci.wedeploy.io")
			.delete("movies")
			.execute();

		System.out.println(response.getBody());

		return response.toString();
	}

}