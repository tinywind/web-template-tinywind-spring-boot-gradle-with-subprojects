package org.tinywind.server.config;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.AutoMappingUnknownColumnBehavior;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.LocalCacheScope;
import org.apache.ibatis.type.*;
import org.tinywind.server.Constants;
import org.tinywind.server.util.ClassUtils;
import org.tinywind.server.util.mybatis.BooleanHandler;
import org.tinywind.server.util.mybatis.CodeHasable;
import org.tinywind.server.util.mybatis.CodeHasableHandler;
import org.tinywind.server.util.mybatis.UuidHandler;

import java.util.Arrays;

@Slf4j
public class MybatisConfig {

    @SneakyThrows
    public static Configuration createConfiguration() {
        final Configuration config = new Configuration();
        config.setUseGeneratedKeys(true);
        config.setDefaultExecutorType(ExecutorType.REUSE);
        config.setCacheEnabled(true);
        config.setLazyLoadingEnabled(true);
        config.setAutoMappingUnknownColumnBehavior(AutoMappingUnknownColumnBehavior.WARNING);
        config.setDefaultStatementTimeout(400);
        config.setDefaultFetchSize(100);
        config.setMapUnderscoreToCamelCase(true);
        config.setLocalCacheScope(LocalCacheScope.STATEMENT);
        config.setJdbcTypeForNull(JdbcType.NULL);

        config.getTypeHandlerRegistry().register(BooleanTypeHandler.class);
        config.getTypeHandlerRegistry().register(DateTypeHandler.class);
        config.getTypeHandlerRegistry().register(DateOnlyTypeHandler.class);
        config.getTypeHandlerRegistry().register(SqlDateTypeHandler.class);
        config.getTypeHandlerRegistry().register(TimeOnlyTypeHandler.class);
        config.getTypeHandlerRegistry().register(SqlTimestampTypeHandler.class);
        config.getTypeHandlerRegistry().register(SqlTimeTypeHandler.class);

        config.getTypeHandlerRegistry().register(java.sql.Timestamp.class, DateTypeHandler.class);
        config.getTypeHandlerRegistry().register(java.sql.Timestamp.class, SqlTimestampTypeHandler.class);
        config.getTypeHandlerRegistry().register(java.sql.Time.class, DateTypeHandler.class);
        config.getTypeHandlerRegistry().register(java.sql.Time.class, SqlTimeTypeHandler.class);
        config.getTypeHandlerRegistry().register(java.sql.Date.class, DateTypeHandler.class);
        config.getTypeHandlerRegistry().register(java.sql.Date.class, SqlDateTypeHandler.class);
        config.getTypeHandlerRegistry().register(java.util.Date.class, DateTypeHandler.class);
        config.getTypeHandlerRegistry().register(java.util.UUID.class, UuidHandler.class);
        config.getTypeHandlerRegistry().register(java.lang.Boolean.class, BooleanHandler.class);

        config.getTypeAliasRegistry().registerAlias("uuid", java.util.UUID.class);
        Arrays.asList(Constants.MODEL_PACKAGE).forEach(aPackage -> config.getTypeAliasRegistry().registerAliases(aPackage));

        ClassUtils.getClasses(Constants.BASE_PACKAGE, CodeHasable.class).stream()
                .filter(e -> !e.isInterface())
                .distinct()
                .map(e -> new CodeHasableHandler(e))
                .forEach(handler -> config.getTypeHandlerRegistry().register(handler));

        ClassUtils.getClasses().stream()
                .filter(e -> e.isEnum() && !ClassUtils.isExpands(e, CodeHasable.class))
                .distinct()
                .map(e -> new EnumTypeHandler(e))
                .forEach(handler -> config.getTypeHandlerRegistry().register(handler));

        return config;
    }
}
