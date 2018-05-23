package connection.cassandra;

import model.CommonConstants;

public enum Query {

    GET_ALL_PRODUCTS("SELECT * from " + CommonConstants.TABLE_PRODUCT + ";"),
    GET_PRODUCT_BY_ID("SELECT * from " + CommonConstants.TABLE_PRODUCT + " where id = ?;"),
    INSERT_PRODUCTS("INSERT INTO " + CommonConstants.TABLE_PRODUCT + CommonConstants.COLUMNS + " values(?, ?, ?, ?, ?);"),
    UPDATE_PRODUCTS("UPDATE " + CommonConstants.TABLE_PRODUCT + " set title=?, description=?, price=?, image=? where id =? ;");

    private final String queryString;

    Query(String queryString) {
        this.queryString = queryString;
    }

    /**
     * find the enum type by a given name
     *
     * @param name
     * @return
     */
    public static Query findByName(String name) {
        // any work to do?
        if (name == null) {
            return null;
        }

        for (final Query type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return queryString;
    }
}
