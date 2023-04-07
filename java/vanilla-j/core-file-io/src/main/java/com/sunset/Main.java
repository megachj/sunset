package com.sunset;

public class Main {

    public static void main(String[] args) throws Exception {
        SqlCreationService sqlCreationService = new SqlCreationService();
        sqlCreationService.createDummySqlFile();
    }
}
