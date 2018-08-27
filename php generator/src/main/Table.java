package main;

import java.util.ArrayList;
import java.util.Arrays;

public class Table {

    private String name;
    private ArrayList<String> columns;

    public Table(String name, String... columns) {
        this.name = name;

        this.columns = new ArrayList<>();
        this.columns.addAll(Arrays.asList(columns));
    }

    /**
     * Return the all columns concatenated like
     * @return id, name, lastname...
     */
    public String concatenatedColumns() {
        return this.concatenatedColumns("", "");
    }
  
    /**
     * Return the all columns concatenated with a string preceding each column
     * @param before The string preceding each column
     * @return For example $id, $name, $lastname...
     */
    public String concatenatedColumns(String before) {
        return this.concatenatedColumns(before, "");
    }
  
    /**
     * Return the all columns concatenated with a string preceding each column
     * @param before The string preceding each column
     * @param after The string after each column
     * @return 
     */
    public String concatenatedColumns(String before, String after) {
        String s = "";
        
        for (String column : this.columns) {
            s += before + column + after + ", ";
        }
        
        /* remove the last ', ' of the string  */
        s = s.substring(0, s.length() -2);
        
        return s;
    }
    
    // <editor-fold defaultstate="collapsed" desc="getters y setters">
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getColumns() {
        return columns;
    }

    public void setColumns(ArrayList<String> columns) {
        this.columns = columns;
    }

    // </editor-fold>
    
}