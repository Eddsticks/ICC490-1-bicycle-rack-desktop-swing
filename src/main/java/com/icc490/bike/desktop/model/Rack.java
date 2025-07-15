package com.icc490.bike.desktop.model;

public class Rack {
    private Long id;
    private Long rows;
    private Long columns;
    private Long totalHooks;

    public Rack() {
    }

    public Rack(Long id, Long rows, Long columns, Long totalHooks) {
        this.id = id;
        this.rows = rows;
        this.columns = columns;
        this.totalHooks = totalHooks;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Long getRows() {
        return rows;
    }

    public Long getColumns() {
        return columns;
    }

    public Long getTotalHooks() {
        return totalHooks;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setRows(Long rows) {
        this.rows = rows;
    }

    public void setColumns(Long columns) {
        this.columns = columns;
    }

    public void setTotalHooks(Long totalHooks) {
        this.totalHooks = totalHooks;
    }

    @Override
    public String toString() {
        return "Rack{" +
                "id=" + id +
                ", rows=" + rows +
                ", columns=" + columns +
                ", totalHooks=" + totalHooks +
                '}';
    }
}