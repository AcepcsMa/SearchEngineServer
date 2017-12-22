package search.data;

/**
 * Class of query.
 */
public class Query {

	private String query;
	private int page;
	private String startAt;
	private String endAt;

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
		return startAt;
	}

	public void setStartAt(String startAt) {
		this.startAt = startAt;
	}

	public String getEndAt() {
		return endAt;
	}

	public void setEndAt(String endAt) {
		this.endAt = endAt;
	}

	@Override
	public String toString() {
		return "Query{" +
				"query='" + query + '\'' +
				", page=" + page +
				", startAt='" + startAt + '\'' +
				", endAt='" + endAt + '\'' +
				'}';
	}
}
