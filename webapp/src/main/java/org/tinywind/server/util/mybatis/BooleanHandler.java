package org.tinywind.server.util.mybatis;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class BooleanHandler implements TypeHandler<Boolean> {

    @Override
    public void setParameter(PreparedStatement ps, int i, Boolean parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter ? 1 : 0);
    }

    @Override
    public Boolean getResult(ResultSet rs, String columnName) throws SQLException {
        return toBoolean(rs.getString(columnName));
    }

    @Override
    public Boolean getResult(ResultSet rs, int columnIndex) throws SQLException {
        return toBoolean(rs.getString(columnIndex));
    }

    @Override
    public Boolean getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return toBoolean(cs.getString(columnIndex));
    }

    private Boolean toBoolean(String value) {
        if (StringUtils.isEmpty(value))
            return null;

        return Objects.equals(value, "1");
    }
}
