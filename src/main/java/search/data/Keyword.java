package search.data;

/**
 * Class of Keyword.
 */
public class Keyword implements Comparable<Keyword>{

	private String word;
	private long count;

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "Keyword{" +
				"word='" + word + '\'' +
				", count=" + count +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Keyword keyword = (Keyword) o;

		if (getCount() != keyword.getCount()) return false;
		return getWord().equals(keyword.getWord());
	}

	@Override
	public int hashCode() {
		int result = getWord().hashCode();
		result = 31 * result + (int) (getCount() ^ (getCount() >>> 32));
		return result;
	}

	@Override
	public int compareTo(Keyword keyword) {
		if(word.equals(keyword.getWord())) {
			return Long.compare(keyword.getCount(), count);
		}
		return word.compareTo(keyword.getWord());
	}
}
