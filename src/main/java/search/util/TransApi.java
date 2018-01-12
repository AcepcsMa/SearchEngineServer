package search.util;

import java.util.HashMap;
import java.util.Map;

public class TransApi {
	private static final String TRANS_API_HOST = "http://api.fanyi.baidu.com/api/trans/vip/translate";
	private static final String PARAM_QUERY = "q";
	private static final String PARAM_FROM = "from";
	private static final String PARAM_TO = "to";
	private static final String PARAM_APP_ID = "appid";
	private static final String PARAM_SALT = "salt";
	private static final String PARAM_SIGN = "sign";

	private String appid;
	private String securityKey;

	public TransApi(String appid, String securityKey) {
		this.appid = appid;
		this.securityKey = securityKey;
	}

	public String getTransResult(String query, String from, String to) throws Exception{
		Map<String, String> params = buildParams(query, from, to);
		return Get.get(TRANS_API_HOST, params);
	}

	private Map<String, String> buildParams(String query, String from, String to) throws Exception{
		Map<String, String> params = new HashMap<>();
		params.put(PARAM_QUERY, query);
		params.put(PARAM_FROM, from);
		params.put(PARAM_TO, to);
		params.put(PARAM_APP_ID, appid);

		// generate a random number
		String salt = String.valueOf(System.currentTimeMillis());
		params.put(PARAM_SALT, salt);

		// sinature
		String src = appid + query + salt + securityKey;
		params.put(PARAM_SIGN, MD5.md5(src));

		return params;
	}

}
