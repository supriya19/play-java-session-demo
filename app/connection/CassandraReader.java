package connection;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Session;

public class CassandraReader implements SessionReader {

    private final Session session;

    public CassandraReader(SessionManager session) {
        this.session = session.getSession();
    }

    @Override
    public ResultSet getDataFromDatabase(BoundStatement query) {
        return session.execute(query);
    }

    @Override
    public ResultSetFuture getDataListFromStatement(BoundStatement query) {
        return session.executeAsync(query);
    }
}
