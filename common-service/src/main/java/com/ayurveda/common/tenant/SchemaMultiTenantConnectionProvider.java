package com.ayurveda.common.tenant;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;

/**
 * Opens a JDBC connection and sets PostgreSQL {@code search_path} to the tenant schema.
 */
public class SchemaMultiTenantConnectionProvider implements MultiTenantConnectionProvider<String> {

    private final DataSource dataSource;

    public SchemaMultiTenantConnectionProvider(DataSource dataSource) {
        this.dataSource = dataSource instanceof TenantAwareDataSource tenantAware
                ? tenantAware.getTargetDataSource()
                : dataSource;
    }

    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        Connection connection = getAnyConnection();
        applySchema(connection, tenantIdentifier);
        return connection;
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        try {
            applySchema(connection, TenantIdentifierResolver.DEFAULT_SCHEMA);
        } catch (SQLException ignored) {
            // connection is being closed regardless
        }
        connection.close();
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(Class<?> unwrapType) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        throw new UnsupportedOperationException("Cannot unwrap " + unwrapType);
    }

    private void applySchema(Connection connection, String schema) throws SQLException {
        String searchPath = TenantSchemaNames.isHospitalSchema(schema)
                ? schema
                : TenantIdentifierResolver.DEFAULT_SCHEMA;
        connection.setSchema(searchPath);
        if (TenantSchemaNames.isHospitalSchema(schema)) {
            String current = connection.getSchema();
            if (current == null || !schema.equalsIgnoreCase(current)) {
                throw new SQLException(
                        "Hospital schema '" + schema + "' is not active on the JDBC connection (current="
                                + current + "). Provision the hospital schema before clinical writes.");
            }
        }
    }
}
