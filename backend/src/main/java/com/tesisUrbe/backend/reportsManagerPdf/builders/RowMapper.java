package com.tesisUrbe.backend.reportsManagerPdf.builders;

import java.util.List;

@FunctionalInterface
public interface RowMapper<T> {
    List<String> mapRow(T record);
}
