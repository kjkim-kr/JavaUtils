package kr.kj.javautils.mysql;

import kr.kj.javautils.pystring.PyString;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

public class MysqlModule {
    // required params;
    private String userID;
    private String userPassword;
    private String databaseUrl;
    private String dbName;

    // optional params;
    private String dbPort;
    private String charEncoding;

    private String baseSplitChar;

    // member
    private Connection conn; // DB 커넥션 연결 객체
    private Statement state;


    public MysqlModule(Builder builder) {
        this.userID = builder.userID;
        this.userPassword = builder.userPassword;
        this.databaseUrl = builder.databaseUrl;
        this.dbName = builder.dbName;

        this.charEncoding = builder.charEncoding;
        this.dbPort = builder.dbPort;

        this.baseSplitChar = builder.baseSplitChar;
    }


    public ArrayList<String> runQuery(String query, ArrayList<String> keySet, String splitChar){
        if(conn == null && state == null) return null;
        ArrayList<String> resultList = new ArrayList<String>();

        try {
            ResultSet rs = state.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            if(keySet == null)
                keySet = new ArrayList<String>();

            for (int i = 1; i <= columnCount; i++)
                keySet.add(rsmd.getColumnName(i));

            PyString ps = new PyString(splitChar);
            while(rs.next()) {
                ArrayList<String> items = new ArrayList<String>();

                for(int i = 0; i < columnCount; i++)
                    items.add(rs.getString(keySet.get(i)));

                resultList.add(ps.join(items));
            }
        }
        catch (SQLException se) {
            se.printStackTrace();
        }

        return resultList;
    }

    public ArrayList<String> runQuery(String query){
        return runQuery(query, null, baseSplitChar);
    }
    public ArrayList<String> runQuery(String query, ArrayList<String> keySet){
        return runQuery(query, keySet, baseSplitChar);
    }

    public void connect() {
        Properties props = new Properties();

        props.put("user", userID);
        props.put("password", userPassword);

        if(charEncoding != null && !charEncoding.isEmpty())
            props.put("characterEncoding", charEncoding);

        String dbUrl = String.format("jdbc:mysql://%s:%s/%s", databaseUrl, dbPort, dbName);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(dbUrl, props);
            state = conn.createStatement();
        }
        catch (ClassNotFoundException cfe) {
            cfe.printStackTrace();
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void close() {
        if(conn != null && state != null) {
            try {
                conn.close();
                state.close();
            }
            catch(SQLException se) {
                se.printStackTrace();
            }
        }
    }


    public static class Builder {
        // required params;
        private String userID;
        private String userPassword;
        private String databaseUrl;
        private String dbName;

        // optional params;
        private String dbPort = "3306";
        private String charEncoding;
        private String baseSplitChar = ",";

        public Builder(String userID, String userPassword,
                       String databaseUrl, String dbName) {
            this.userID = userID;
            this.userPassword = userPassword;
            this.databaseUrl = databaseUrl;
            this.dbName = dbName;
        }

        public Builder setDatabasePort(String dbPort) {
            this.dbPort = dbPort;
            return this;
        }

        public Builder setCharEncoding(String charEncoding) {
            this.charEncoding = charEncoding;
            return this;
        }

        public Builder setBaseSplitChar(String baseSplitChar){
            this.baseSplitChar = baseSplitChar;
            return this;
        }

        public MysqlModule build() {
            return new MysqlModule(this);
        }
    }
}
