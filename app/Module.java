import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import connection.CassandraReader;
import connection.SessionManager;
import connection.SessionReader;
import connection.cassandra.CassandraQuery;
import connection.cassandra.Query;
import model.CommonConstants;
import services.ApplicationTimer;
import services.AtomicCounter;
import services.Counter;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.time.Clock;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.
 * <p>
 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
public class Module extends AbstractModule {


    @Inject
    @Named(CommonConstants.DATA_READER)
    SessionReader sessionReadOnly;

    @Override
    public void configure() {
        // Use the system clock as the default implementation of Clock
        bind(Clock.class).toInstance(Clock.systemDefaultZone());
        // Ask Guice to create an instance of ApplicationTimer when the
        // application starts.
        bind(ApplicationTimer.class).asEagerSingleton();
        // Set AtomicCounter as the implementation for Counter.
        bind(Counter.class).to(AtomicCounter.class);
        /*bind(CassandraReader.class).annotatedWith(Names.named(CommonConstants.DATA_READER))
                .toInstance(sessionReadOnly);*/
        bind(SessionReader.class).annotatedWith(Names.named(CommonConstants.DATA_READER))
                .to(Key.get(CassandraReader.class, Names.named(CommonConstants.DATA_READER)));
    }

    @Provides
    @Singleton
    @Named(CommonConstants.CONFIG)
    public Config getConfig() {
        return ConfigFactory.load();
    }

    @Provides
    @Singleton
    @Named(CommonConstants.CASSANDRA_CLUSTER)
    public Cluster getCassandraCluster(@com.google.inject.name.Named(CommonConstants.CONFIG) Config config) {
        final List<String> cassandraContactPoints = config.getStringList(CommonConstants.CASSANDRA_DB_HOST_NAME);
        final Cluster.Builder builder = Cluster.builder();
        for (final String cassandraContactPoint : cassandraContactPoints) {
            builder.addContactPoint(cassandraContactPoint);
        }
        builder.withPort(Integer.parseInt(config.getString(CommonConstants.CASSANDRA_DB_PORT)));

        final Cluster cluster = builder.build();
        // to log queries
        /*final com.datastax.driver.core.QueryLogger queryLogger = com.datastax.driver.core.QueryLogger.builder().build();
        cluster.register(queryLogger);*/
        return cluster;
    }

    @Provides
    @Singleton
    @javax.inject.Named(CommonConstants.DATA_READER)
    public CassandraReader createDataReader(@com.google.inject.name.Named(CommonConstants.SESSION_MANAGER) SessionManager sessionManger) {
        return new CassandraReader(sessionManger);
    }

    @Provides
    @Singleton
    @Named(CommonConstants.SESSION_MANAGER)
    public SessionManager getTotesSessionProvider(@com.google.inject.name.Named(CommonConstants.CASSANDRA_CLUSTER) Cluster cassandraCluster,
                                                  @com.google.inject.name.Named(CommonConstants.CONFIG) Config config) {
        return new SessionManager(cassandraCluster, config.getString(CommonConstants.CASSANDRA_DB_KEYSPACE));
    }

    public Map<Query, PreparedStatement> prepareStatements(Map<Query, String> statements, Session session) {
        final Map<Query, PreparedStatement> preparedStatements = statements.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entrySet -> session.prepare(entrySet.getValue())));
        return new HashMap<>(preparedStatements);
    }

    @Provides
    @Singleton
    @Named("cassandraQueries")
    public CassandraQuery createCassandraQuery(@com.google.inject.name.Named(CommonConstants.SESSION_MANAGER)
                                                           SessionManager sessionManger) {
        Map<Query, String> statementsToBePrepared = Stream.of(Query.values()).collect(Collectors.toMap(query -> query, Query::toString));
        Map<Query, PreparedStatement> queryPreparedStatementEnumMap =
                prepareStatements(statementsToBePrepared, sessionManger.getSession());
        return new CassandraQuery(queryPreparedStatementEnumMap);
    }
}
