package com.dolphin.adminbackend.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DynamicQueryRepo {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    public List<Object[]> runNativeQuery(String sqlQuery) {
        Query query = entityManager.createNativeQuery(sqlQuery);
        return query.getResultList();
    }

    public List<Map<String, Object>> executeDynamicQuery(String sql)
            throws org.springframework.jdbc.BadSqlGrammarException {
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            Map<String, Object> row = new HashMap<>();

            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                Object columnValue = rs.getObject(i);

                // Convert TIMESTAMP columns to formatted string
                if (columnValue instanceof java.sql.Timestamp) {
                    columnValue = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(columnValue);
                }

                row.put(columnName, columnValue);
            }
            return row;
        });
    }

}
