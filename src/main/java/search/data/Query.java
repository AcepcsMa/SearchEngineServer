package search.data;

import java.time.LocalDateTime;

/**
 * Class of query.
 */
public class Query {

	public static final String DEFAULT_START_AT = "1970-01-01";
	private String query;
	private int page;
	private String startAt;
	private String endAt;
	private int size;

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public String getStartAt() {
		return startAt == null ? DEFAULT_START_AT : startAt;
	}

	public void setStartAt(String startAt) {
		this.startAt = startAt;
	}

	public String getEndAt() {
		return endAt == null ? LocalDateTime.now().toLocalDate().toString() : endAt;
	}

	public void setEndAt(String endAt) {
		this.endAt = endAt;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public String toString() {
		return "Query{" +
				"query='" + query + '\'' +
				", page=" + page +
				", startAt='" + startAt + '\'' +
				", endAt='" + endAt + '\'' +
				", size=" + size +
				'}';
	}
}
