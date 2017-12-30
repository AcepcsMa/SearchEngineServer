package search.controller;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import search.data.Query;
import search.data.SearchResult;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.SuggestionBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import search.util.LanguageService;

import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The main search service controller.
 */
@Controller
@CrossOrigin
public class MainSearchController {

    public static final String DEFAULT_START_AT = "1970-01-01";
    public static final int RESULT_PER_PAGE = 10;
    public static final String DEFAULT_INDEX = "albums";
    public static final String DEFAULT_TYPE = "album";
    public static final String PARAM_PUB_TIME = "pub_time";
    public static final String PARAM_TITLE = "title";
    public static final String PARAM_TITLE_PINYIN = "title.pinyin";
    public static final String PARAM_TITLE_ENGLISH = "title.english";
    public static final String PARAM_CONTENT = "content";
    public static final String PARAM_AVATAR_URL = "avatar_url";
	public static final String PARAM_PIC_COUNT = "pic_count";
    public static final String PARAM_URL = "url";
    public static final String PARAM_SUGGEST_FIELD = "suggest";
    public static final String PARAM_SUGGEST_NAME = "suggest_album";
	public static final String PARAM_APP_ID = "APP_ID";
	public static final String PARAM_SECURITY_KEY = "SECURITY_KEY";
    public static final String CONFIG_PATH = "config/server.conf";
    public static final String ELASTIC_HOST = "ELASTIC_HOST";
	public static final String ELASTIC_PORT = "ELASTIC_PORT";
	public static final String ELASTIC_SCHEME = "ELASTIC_SCHEME";

	public String elasticHost;
	public int elasticPort;
	public String elasticScheme;

	public LanguageService languageService;

	private static Logger logger = LogManager.getLogger(MainSearchController.class);

	public MainSearchController() {

		JsonParser parser = new JsonParser();
		try {
			JsonObject jsonObject = (JsonObject)parser.parse(new FileReader(CONFIG_PATH));
			this.elasticHost = jsonObject.get(ELASTIC_HOST).getAsString();
			this.elasticPort = jsonObject.get(ELASTIC_PORT).getAsInt();
			this.elasticScheme = jsonObject.get(ELASTIC_SCHEME).getAsString();
			this.languageService = new LanguageService(jsonObject.get(PARAM_APP_ID).getAsString(),
					jsonObject.get(PARAM_SECURITY_KEY).getAsString());
		} catch (IOException e) {
			throw new RuntimeException();
		}

	}

    /**
     * Home page controller.
     * @return the home page
     */
    @GetMapping("/")
    public String homePage() {

    	logger.info("VISIT");
        return "homePage";
    }

    /**
     * Search service controller.
     * @param map map of html template
     * @param query query string
     * @param start start date
     * @param end end date
     * @return search result page
     */
    @GetMapping("/search")
    public String receiveQuery(Map<String, Object> map,
                               @RequestParam(value = "query", required = true) String query,
                               @RequestParam(value = "start", required = false) String start,
                               @RequestParam(value = "end", required = false) String end) {

    	logger.info("QUERY:" + query);
        map.put("query", query);
        return "resultPage";
    }

    /**
     * Load search data controller.
     * @param query actual query object
     * @return a list of search results
     * @throws Exception exceptions
     */
    @PostMapping("/loadSearchResult")
    @ResponseBody
    public List<SearchResult> getSearchResult(@RequestBody Query query) throws Exception{

        List<SearchResult> results = new ArrayList<>();

        // get query parameter
        String startAt = query.getStartAt();
        String endAt = query.getEndAt();
        int pageCount = query.getPage();
        String queryStr = query.getQuery();
        int size = query.getSize();

        // build elastic query
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        QueryBuilder elasticQuery;

        // language detection
        if(languageService.isEnglish(queryStr)) {
        	String chineseQuery = languageService.fromEnglish(queryStr);
			elasticQuery = QueryBuilders.boolQuery()
					.must(QueryBuilders.rangeQuery(PARAM_PUB_TIME).gte(startAt).lte(endAt))
					.should(QueryBuilders.matchQuery(PARAM_TITLE, chineseQuery))
					.should(QueryBuilders.matchQuery(PARAM_TITLE_ENGLISH, queryStr))
					.minimumShouldMatch(1);
		} else {
			String englishQuery = languageService.toEnglish(queryStr);
			elasticQuery = QueryBuilders.boolQuery()
					.must(QueryBuilders.rangeQuery(PARAM_PUB_TIME).gte(startAt).lte(endAt))
					.should(QueryBuilders.matchQuery(PARAM_TITLE, queryStr))
					.should(QueryBuilders.matchQuery(PARAM_TITLE_ENGLISH, englishQuery))
					.minimumShouldMatch(1);
		}

		// deal with different page size
        sourceBuilder.query(elasticQuery);
        if(size != RESULT_PER_PAGE) {
			sourceBuilder.from(pageCount * RESULT_PER_PAGE).size(size);
		} else {
			sourceBuilder.from(pageCount * RESULT_PER_PAGE).size(RESULT_PER_PAGE);
		}

		// build highlighter
		sourceBuilder.highlighter(new HighlightBuilder()
				.field("title")
				.highlighterType("plain")
				.field("title.english")
				.highlighterType("plain")
				.preTags("<span style=\"color:yellow\">")
				.postTags("</span>"));
        SearchRequest searchRequest = new SearchRequest(DEFAULT_INDEX);
        searchRequest.types(DEFAULT_TYPE);
        searchRequest.source(sourceBuilder);

		// send query to elastic-search and parse response
		RestHighLevelClient client = null;
		try {
			client = new RestHighLevelClient(
					RestClient.builder(new HttpHost(elasticHost, elasticPort, elasticScheme)));
			SearchResponse response = client.search(searchRequest);
			SearchHits hits = response.getHits();
			for (SearchHit hit : hits) {
				Map<String, Object> source = hit.getSourceAsMap();
				SearchResult result = new SearchResult();
				try {
					result.setTitle(hit.getHighlightFields().get("title").fragments()[0].string());
				} catch (Exception e) {
					result.setTitle(hit.getHighlightFields().get("title.english").fragments()[0].string());
				}
				result.setDescription((String)source.get(PARAM_CONTENT));
				result.setAvatarUrl((String)source.get(PARAM_AVATAR_URL));
				result.setUrl((String)source.get(PARAM_URL));
				result.setPicCount(Integer.parseInt((String.valueOf(source.get(PARAM_PIC_COUNT)))));
				results.add(result);
			}
		} catch (IOException e) {
			logger.error("ERROR:" + e.toString());
		} finally {
			if(client != null) {
				try {
					client.close();
				} catch (IOException e) {
					logger.error("ERROR:" + e.toString());
				}
			}
		}

        return results;
    }

    /**
     * Auto complete service controller.
     * @param prefix query prefix
     * @return a list of auto-completion suggestions
     * @throws Exception exceptions
     */
    @GetMapping(value="/ac")
    @ResponseBody
    public List<String> getAutoCompletion(
            @RequestParam(value = "term", required = true) String prefix) {

        List<String> results = new ArrayList<>();

        // build elastic query
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        SuggestionBuilder termSuggestionBuilder = SuggestBuilders
                .completionSuggestion(PARAM_SUGGEST_FIELD)
                .text(prefix);
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder.addSuggestion(PARAM_SUGGEST_NAME, termSuggestionBuilder);
        searchSourceBuilder.suggest(suggestBuilder);
        SearchRequest searchRequest = new SearchRequest(DEFAULT_INDEX);
        searchRequest.types(DEFAULT_TYPE);
        searchRequest.source(searchSourceBuilder);

		RestHighLevelClient client = null;
		try {
			client = new RestHighLevelClient(
					RestClient.builder(new HttpHost(elasticHost, elasticPort, elasticScheme)));
			// send query and parse the response
			SearchResponse response = client.search(searchRequest);
			Suggest suggestions = response.getSuggest();
			CompletionSuggestion terms = suggestions.getSuggestion(PARAM_SUGGEST_NAME);

			for(CompletionSuggestion.Entry entry : terms.getEntries()) {
				for(CompletionSuggestion.Entry.Option option : entry) {
					results.add((String)option.getHit().getSourceAsMap().get(PARAM_TITLE));
				}
			}
		} catch (IOException e) {
			logger.error("ERROR:" + e.toString());
		} finally {
			if(client != null) {
				try {
					client.close();
				} catch (IOException e) {
					logger.error("ERROR:" + e.toString());
				}
			}
		}

        return results;
    }
}
