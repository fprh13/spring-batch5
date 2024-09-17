package org.example.springbatch.entity;

import org.springframework.jdbc.core.RowMapper;


import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Sixth 배치 JdbcPagingItemReader 에서 사용하는 매퍼
 */
public class CustomBeforeRowMapper implements RowMapper<BeforeEntity> {

    public static final String ID_COLUMN = "id";
    public static final String USERNAME_COLUMN = "username";

    @Override
    public BeforeEntity mapRow(ResultSet rs, int rowNum) throws SQLException {

        BeforeEntity beforeEntity = new BeforeEntity();

        beforeEntity.setId(rs.getLong(ID_COLUMN));
        beforeEntity.setUsername(rs.getString(USERNAME_COLUMN));

        return beforeEntity;
    }
}
