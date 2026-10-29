package org.tinywind.server.security;

import org.junit.jupiter.api.Test;
import org.tinywind.server.util.mybatis.UuidHandler;

import javax.sql.rowset.RowSetProvider;
import javax.sql.rowset.RowSetMetaDataImpl;
import java.sql.Types;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UuidCompatibilityTest {
    @Test void readsEveryHexDigitFromOracleRawIdentifier() throws Exception {
        var row = RowSetProvider.newFactory().createCachedRowSet();
        var metadata = new RowSetMetaDataImpl();
        metadata.setColumnCount(1);
        metadata.setColumnName(1, "id");
        metadata.setColumnType(1, Types.VARCHAR);
        row.setMetaData(metadata);
        row.moveToInsertRow();
        row.updateString(1, "123456789ABCDEF0123456789ABCDEF0");
        row.insertRow();
        row.moveToCurrentRow();
        row.beforeFirst();
        row.next();
        assertEquals(UUID.fromString("12345678-9abc-def0-1234-56789abcdef0"), new UuidHandler().getResult(row, "id"));
    }
}
