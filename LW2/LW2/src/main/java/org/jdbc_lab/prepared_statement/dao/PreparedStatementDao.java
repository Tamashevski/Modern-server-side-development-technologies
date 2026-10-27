package org.jdbc_lab.prepared_statement.dao;

import java.util.List;

public interface PreparedStatementDao<T> {
    void clearTable();
    void insert(T t);
    int getCount();
    List<T> getAll();
    T getById(Long id);
}
