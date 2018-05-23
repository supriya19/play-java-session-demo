package connection.cassandra;

import com.datastax.driver.core.PreparedStatement;

import java.util.Map;

public class CassandraQuery {


    private final Map<Query, PreparedStatement> preparedStatementMap;

    public CassandraQuery(Map<Query, PreparedStatement> preparedStatementMap) {
        this.preparedStatementMap = preparedStatementMap;
    }

    public Map<Query, PreparedStatement> getPreparedStatementMap() {
        return preparedStatementMap;
    }
}
