package connection;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;

import javax.inject.Named;
import java.io.Serializable;

@Named
public interface SessionReader extends Serializable {
    ResultSet getDataFromDatabase(BoundStatement query);
    ResultSetFuture getDataFromDatabaseAsync(BoundStatement query);
}
