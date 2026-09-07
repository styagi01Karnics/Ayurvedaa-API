package com.ayurveda.common.tenant;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DelegatingDataSource;

/**
 * Sets PostgreSQL {@code search_path} on every connection checkout from {@link TenantContext}.
 * Resets to {@code public} when no hospital schema is bound (startup / health).
 */
public class TenantAwareDataSource extends DelegatingDataSource {

    public TenantAwareDataSource(DataSource targetDataSource) {
        super(targetDataSource);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return checkoutAndApplySchema(super.getConnection());
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return checkoutAndApplySchema(super.getConnection(username, password));
    }

    private Connection checkoutAndApplySchema(Connection connection) throws SQLException {
        try {
            applySearchPath(connection);
            return connection;
        } catch (SQLException | RuntimeException ex) {
            closeQuietly(connection);
            throw ex;
        }
    }

    private void applySearchPath(Connection connection) throws SQLException {
        String schema = TenantContext.getSchemaName();
        String searchPath = TenantSchemaNames.isHospitalSchema(schema) ? schema : "public";
        connection.setSchema(searchPath);
        if (TenantSchemaNames.isHospitalSchema(schema)) {
            String current = connection.getSchema();
            if (current == null || !schema.equalsIgnoreCase(current)) {
                throw new SQLException(
                        "Hospital schema '" + schema + "' is not active (current=" + current + ")");
            }
        }
    }

    private static void closeQuietly(Connection connection) {
        try {
            connection.close();
        } catch (SQLException ignored) {
            // returning the connection to Hikari is the goal; the original error is rethrown
        }
    }

    /**
     * Unwrap may be used by pools/tools; keep target accessible.
     */
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return iface.cast(this);
        }
        return super.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this) || super.isWrapperFor(iface);
    }

}
