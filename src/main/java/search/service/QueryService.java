package search.service;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;

public class QueryService {

	public static final String PARAM_PUB_TIME = "pub_time";
	public static final String PARAM_TITLE = "title";
	public static final String PARAM_TITLE_ENGLISH = "title.english";
	public static final String PARAM_CONTENT = "content";
	public static final String PARAM_AVATAR_URL = "avatar_url";
	public static final String PARAM_PIC_COUNT = "pic_count";
	public static final String PARAM_URL = "url";

	public static QueryBuilder buildQuery(LanguageService lang, String queryStr, String startAt, String endAt) throws Exception{
		QueryBuilder elasticQuery;

		// language detection
		if(lang.isEnglish(queryStr)) {
			String chineseQuery = lang.fromEnglish(queryStr);
			elasticQuery = QueryBuilders.boolQuery()
					.must(QueryBuilders.rangeQuery(PARAM_PUB_TIME).gte(startAt).lte(endAt))
					.should(QueryBuilders.matchQuery(PARAM_TITLE, chineseQuery))
					.should(QueryBuilders.matchQuery(PARAM_TITLE_ENGLISH, queryStr))
					.minimumShouldMatch(1);
		} else {
			String englishQuery = lang.toEnglish(queryStr);
			elasticQuery = QueryBuilders.boolQuery()
					.must(QueryBuilders.rangeQuery(PARAM_PUB_TIME).gte(startAt).lte(endAt))
					.should(QueryBuilders.matchQuery(PARAM_TITLE, queryStr))
					.should(QueryBuilders.matchQuery(PARAM_TITLE_ENGLISH, englishQuery))
					.minimumShouldMatch(1);
		}
		return elasticQuery;
	}

	public static SearchSourceBuilder getSourceBuilder(int startIndex, int count) {
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.from(startIndex).size(count);
		sourceBuilder.highlighter(new HighlightBuilder()
				.field("title")
				.highlighterType("plain")
				.field("title.english")
				.highlighterType("plain")
				.preTags("<span style=\"color:yellow\">")
				.postTags("</span>"));
		return sourceBuilder;
	}
}
