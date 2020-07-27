package org.tinywind.server.util.mybatis;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeException;
import org.apache.ibatis.type.TypeHandler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @author tinywind
 * @since 2017-05-14
 */
public class CodeHasableHandler<E extends Enum<E> & CodeHasable<?>> implements TypeHandler<E> {
    private Class<E> type;

    @SuppressWarnings("unchecked")
    public CodeHasableHandler() {
        Type genericSuperclass = getClass().getGenericSuperclass();
        if (!(genericSuperclass instanceof ParameterizedType))
            return;

        Type[] actualTypeArguments = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
        if (actualTypeArguments.length < 1)
            return;

        type = (Class<E>) actualTypeArguments[0];
    }

    public CodeHasableHandler(Class<E> type) {
        this.type = type;
    }

    public Class<E> getType() {
        return type;
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, Objects.requireNonNull(parameter.getCode()).toString());
    }

    @Override
    public E getResult(ResultSet rs, String columnName) throws SQLException {
        String code = rs.getString(columnName);
        return valueOf(code);
    }

    @Override
    public E getResult(ResultSet rs, int columnIndex) throws SQLException {
        String code = rs.getString(columnIndex);
        return valueOf(code);
    }

    @Override
    public E getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String code = cs.getString(columnIndex);
        return valueOf(code);
    }

    public E valueOf(String code) {
        for (E value : type.getEnumConstants())
            if (Objects.equals(value.getCode(), code))
                return value;

        throw new TypeException("Can't make enum object " + type);
    }

    public E valueOf(char c) {
        return valueOf(String.valueOf(c));
    }
}
