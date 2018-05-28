
CREATE KEYSPACE sapient WITH replication = {'class': 'NetworkTopologyStrategy', 'dc1': '1', 'dc2': '1'}  AND durable_writes = true;

CREATE TABLE sapient.products (
    id uuid PRIMARY KEY,
    title text,
    description text,
    price text,
	image text
)