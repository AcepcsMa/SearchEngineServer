package search.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Language service.
 */
public class LanguageService {

	private TransApi transApi;

	public LanguageService(String appId, String securityKey) {

		transApi = new TransApi(appId, securityKey);
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

		String transResult = transApi.getTransResult(s, "auto", "en");
		return parseTranslation(transResult);
	}

	/**
	 * Call Baidu Api, translate a string from English to Chinese.
	 * @param s string
	 * @return Chinese string
	 * @throws Exception
	 */
	public String fromEnglish(String s) throws Exception{

		String transResult = transApi.getTransResult(s, "en", "zh");
		return parseTranslation(transResult);
	}

	/**
	 * Parse result from Baidu Api.
	 * @param transResult translation result (JSON)
	 * @return translate result string
	 */
	private String parseTranslation(String transResult) {

		JsonParser parser = new JsonParser();
		JsonObject jsonObject = (JsonObject) parser.parse(transResult);

		StringBuilder builder = new StringBuilder();
		for(JsonElement element : jsonObject.getAsJsonArray("trans_result")) {
			JsonObject obj = (JsonObject) element;
			builder.append(obj.get("dst").getAsString());
		}
		return builder.toString();
	}
}
