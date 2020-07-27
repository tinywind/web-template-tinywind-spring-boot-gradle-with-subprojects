package org.tinywind.server.util.mybatis;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

public class UuidHandler implements TypeHandler<UUID> {

    @Override
    public void setParameter(PreparedStatement ps, int i, UUID parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, Objects.requireNonNull(parameter.toString().replaceAll("-", "").toUpperCase()));
    }

    @Override
    public UUID getResult(ResultSet rs, String columnName) throws SQLException {
        return toUuid(rs.getString(columnName));
    }

    @Override
    public UUID getResult(ResultSet rs, int columnIndex) throws SQLException {
        return toUuid(rs.getString(columnIndex));
    }

    @Override
    public UUID getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return toUuid(cs.getString(columnIndex));
    }

    private UUID toUuid(String value) {
        if (StringUtils.isEmpty(value))
            return null;

        return UUID.fromString(
                value.substring(0, 7)
                        + "-" + value.substring(8, 11)
                        + "-" + value.substring(12, 15)
                        + "-" + value.substring(16, 19)
                        + "-" + value.substring(20, 31)
        );
    }
}
