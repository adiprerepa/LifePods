package main;

import api.ServiceApi;

/**
 * (C) Copyright Aditya Prerepa 2019. All Rights Reserved.
 * adiprerepa@gmail.com
 * todo read credentials from json file - path is env variable
 */

public class Main {
    public static void main(String[] args) throws Throwable {
        ServiceApi serviceApi = new ServiceApi();
        String url = "jdbc:mysql://127.0.0.1/lifepods?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        String user = "aditya";
        String pass = "adityapc";
        int port;
        if (args.length == 0) port = 2001;
        else port = Integer.parseInt(args[0]);
        serviceApi.start(port, url, user, pass);
        serviceApi.blockUntilShutdown();
    }
}