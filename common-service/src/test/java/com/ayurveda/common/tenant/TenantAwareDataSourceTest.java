package com.ayurveda.common.tenant;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class TenantAwareDataSourceTest {

    @AfterEach
    void clearTenant() {
        TenantContext.clear();
    }

    @Test
    void returnsConnectionToPoolWhenSchemaActivationFails() throws SQLException {
        DataSource target = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(target.getConnection()).thenReturn(connection);
        when(connection.getSchema()).thenReturn("public");
        doThrow(new SQLException("schema missing")).when(connection).setSchema("hosp_lot_rj");

        TenantContext.set(UUID.randomUUID(), "LOT-RJ", "hosp_lot_rj");
        TenantAwareDataSource dataSource = new TenantAwareDataSource(target);

        assertThrows(SQLException.class, dataSource::getConnection);
        verify(connection).close();
    }
}
