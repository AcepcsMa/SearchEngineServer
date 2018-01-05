package search.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WordSegmentService {

	public static final String SEGMENT_URL = "http://localhost:3999/split";

	public static List<String> split(String sentence) throws IOException {

		List<String> words = new ArrayList<>();

		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost post = new HttpPost(SEGMENT_URL);

		String jsonStr = "{\"sentence\":\"" + sentence + "\"}";
		StringEntity entity = new StringEntity(jsonStr, ContentType.APPLICATION_JSON);
		entity.setContentEncoding("UTF-8");
		post.setEntity(entity);

		HttpResponse response = client.execute(post);
		BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String line = null;
		StringBuilder result = new StringBuilder();
		while((line = br.readLine()) != null) {
			result.append(line);
		}
		JsonParser parser = new JsonParser();
		JsonArray array = (JsonArray) parser.parse(result.toString());
		Iterator<JsonElement> it = array.iterator();
		while(it.hasNext()) {
			words.add(it.next().getAsString());
		}
		return words;
	}
}