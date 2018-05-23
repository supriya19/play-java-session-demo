package connection;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

import java.util.Optional;

public class SessionManager {

    private final Cluster cluster;
    private final String keyspace;

    private Optional<Session> sessionOptional = Optional.empty();

    public SessionManager(Cluster cluster, String keyspace) {
        this.cluster = cluster;
        this.keyspace = keyspace;
    }

    /**
     * does double check before and after synchronized block to avoid blocking calls
     *
     * @return
     */
    public Session getSession() {
        return sessionOptional.orElseGet(() -> {
            return sessionOptional.orElseGet(() -> {
                final Session session = cluster.connect(keyspace);
                sessionOptional = Optional.of(session);
                return session;
            });
        });
    }
}
