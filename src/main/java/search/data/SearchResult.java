package search.data;

/**
 * Class of search result.
 */
public class SearchResult {

	private String title;			// album title
	private String description;		// album description
	private String url;				// album url
	private String avatarUrl;		// album avatar url
	private int picCount;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public int getPicCount() {
		return picCount;
	}

	public void setPicCount(int picCount) {
		this.picCount = picCount;
	}

	@Override
	public String toString() {
		return "SearchResult{" +
				"title='" + title + '\'' +
				", description='" + description + '\'' +
				", url='" + url + '\'' +
				", avatarUrl='" + avatarUrl + '\'' +
				", picCount=" + picCount +
				'}';
	}
}
