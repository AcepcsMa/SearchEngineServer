package search.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.driver.v1.*;
import search.data.Keyword;

import java.util.Set;
import java.util.TreeSet;

/**
 * Class of neo4j tool.
 */
public class Neo4jTool implements AutoCloseable{
	private  Driver driver;
	private static Logger logger = LogManager.getLogger(Neo4jTool.class);

	public Neo4jTool(String uri, String user, String password) {
		driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
	}

	@Override
	public void close() throws Exception {
		driver.close();
	}

	public Set<Keyword> getConnectedKeywords(String keyword) {
		Set<Keyword> keywords = new TreeSet<>();
		try {
			Session session = driver.session();
			StatementResult result = session.run(String.format("MATCH " +
					"(k1:Keyword)-[r:connects]-(k2:Keyword) " +
					"where k1.value = \"%s\" " +
					"return k2.value, r.count;", keyword));
			for(Record record : result.list()) {
				Keyword kw = new Keyword();
				kw.setCount((Long)record.asMap().get("r.count"));
				kw.setWord(record.asMap().get("k2.value") + " " + keyword);
				keywords.add(kw);
			}
		} catch (Exception e) {
			logger.warn(e.toString());
		}
		return keywords;
	}
}
