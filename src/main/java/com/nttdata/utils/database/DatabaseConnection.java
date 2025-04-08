package com.nttdata.utils.database;


import com.google.gson.Gson;

import java.util.Objects;

public class DatabaseConnection {
    protected String name;
    protected String ip;
    protected String port;
    protected String user;
    protected String pass;
    protected String jdbc;
    protected String schema;
    protected String driverClassName;
    protected String typeConnection;

    public DatabaseConnection() {
    }

    public String getName() {
        return this.name;
    }

    public String getUrl() {
        return "jdbc:" + this.jdbc.trim() + ":thin:@" + this.ip.trim() + ":" + this.port.trim() + this.addSchema();
    }


    public String getTypeConnection() {
        return this.typeConnection;
    }

    protected String addSchema() {
        if (!Objects.isNull(getTypeConnection()))
            if (getTypeConnection().equals("SID"))
                return ":" + this.schema.trim();
        return !this.schema.isEmpty() ? "/" + this.schema.trim() : "";
    }


    public String getUser() {
        return this.user.trim();
    }

    public String getPass() {
        return this.pass.trim();
    }

    public String getDriverClassName() {
        return this.driverClassName.trim();
    }

    public String toString() {
        return (new Gson()).toJson(this);
    }

    public String getIp() {
        return this.ip;
    }

    public String getPort() {
        return this.port;
    }

    public String getJdbc() {
        return this.jdbc;
    }

    public String getSchema() {
        return this.schema;
    }
}

