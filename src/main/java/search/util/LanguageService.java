package search.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Iterator;

/**
 * Language service.
 */
public class LanguageService {

	public static final String ENGLISH = "en";
	public static final String CHINESE = "zh";
	public static final String AUTO = "auto";
	public static final String UTF8 = "UTF-8";

	public static final String CONFIG_PATH = "config/server.conf";
	public static final String PARAM_BASE_TRANSLATE_URL = "BASE_TRANSLATE_URL";
	public static String BASE_TRANSLATE_URL;
	public static Logger logger = LogManager.getLogger(WordSegmentService.class);

	static {
		JsonParser parser = new JsonParser();
		try {
			JsonObject jsonObject = (JsonObject)parser.parse(new FileReader(CONFIG_PATH));
			BASE_TRANSLATE_URL = jsonObject.get(PARAM_BASE_TRANSLATE_URL).getAsString();
		} catch (Exception e) {
			logger.warn(e.toString());
		}
	}

	private TransApi transApi;

	public LanguageService(String appId, String securityKey) {

		transApi = new TransApi(appId, securityKey);
	}

	public String httpPost(String url, String query) throws Exception {

		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost post = new HttpPost(url);

		String jsonStr = "{\"query\":\"" + query + "\"}";
		StringEntity entity = new StringEntity(jsonStr, ContentType.APPLICATION_JSON);
		entity.setContentEncoding(UTF8);
		post.setEntity(entity);

		HttpResponse response = client.execute(post);
		BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String line = null;
		StringBuilder result = new StringBuilder();
		while((line = br.readLine()) != null) {
			result.append(line);
		}
		return result.toString();
	}

	/**
	 * Determine if a string is composed of English completely
	 * @param s string
	 * @return true/false
	 */
	public boolean isEnglish(String s) {

		return s.getBytes().length == s.length();
	}

	/**
	 * Call Baidu Api, translate a string to English.
	 * @param s string
	 * @return English string
	 * @throws Exception
	 */
	public String toEnglish(String s) throws Exception{

		String url = String.format(BASE_TRANSLATE_URL, AUTO, ENGLISH);
		String transResult = httpPost(url, s);
		return parseTranslation(transResult);
	}

	/**
	 * Call Baidu Api, translate a string from English to Chinese.
	 * @param s string
	 * @return Chinese string
	 * @throws Exception
	 */
	public String fromEnglish(String s) throws Exception{

		String url = String.format(BASE_TRANSLATE_URL, ENGLISH, CHINESE);
		String transResult = httpPost(url, s);
		return parseTranslation(transResult);
	}

	/**
	 * Parse result from Baidu Api.
	 * @param transResult translation result (JSON)
	 * @return translate result string
	 */
	private String parseTranslation(String transResult) {

		JsonParser parser = new JsonParser();
		JsonArray array = parser.parse(parser.parse(transResult).getAsString())
				.getAsJsonObject()
				.getAsJsonArray("trans_result");
		StringBuilder builder = new StringBuilder();
		for(JsonElement element : array) {
			JsonObject obj = (JsonObject) element;
			builder.append(obj.get("dst").getAsString());
		}
		return builder.toString();
	}
}
