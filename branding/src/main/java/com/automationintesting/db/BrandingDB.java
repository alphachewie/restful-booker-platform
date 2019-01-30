package com.automationintesting.db;

import com.automationintesting.model.Branding;
import com.automationintesting.model.Contact;
import com.automationintesting.model.Logo;
import com.automationintesting.model.Map;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.Server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BrandingDB {

    private Connection connection;
    private final String SELECT_ALL_BRANDINGS = "SELECT * FROM brandings";
    private final String SELECT_BY_BRANDINGID = "SELECT * FROM brandings WHERE brandingid = ?";
    private final String DELETE_BY_BRANDINGID = "DELETE FROM brandings WHERE brandingid = ?";
    private final String DELETE_ALL_BRANDINGS = "DELETE FROM brandings";

    public BrandingDB() throws SQLException {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:rbp");
        ds.setUser("user");
        ds.setPassword("password");
        connection = ds.getConnection();

        Server server = Server.createTcpServer("-tcpPort", "9092", "-tcpAllowOthers").start();

        String prepareDb = "CREATE TABLE brandings ( brandingid int NOT NULL AUTO_INCREMENT," +
                " hotel_name varchar(255)," +
                " latitude double," +
                " longitude double," +
                " logo_url varchar(255)," +
                " description varchar(2000)," +
                " contact_name varchar(255)," +
                " address varchar(255)," +
                " phone varchar(15)," +
                " email varchar(255)," +
                " primary key (brandingid));";
        connection.prepareStatement(prepareDb).executeUpdate();

        Branding branding = new Branding(
                new Map(),
                new Logo(),
                "Welcome to Shady Meadows, a delightful Bed & Breakfast nestled in the hills on Newingtonfordburyshire. A place so beautiful you will never want to leave. All our rooms have comfortable beds and we provide breakfast from the locally sourced supermarket. It is a delightful place.",
                new Contact()
        );

        InsertSql insertSql = new InsertSql(connection, branding);
        PreparedStatement createBooking = insertSql.getPreparedStatement();

        createBooking.executeUpdate();
    }

    public Branding create(Branding branding) throws SQLException {
        InsertSql insertSql = new InsertSql(connection, branding);
        PreparedStatement createPs = insertSql.getPreparedStatement();

        if(createPs.executeUpdate() > 0) {
            ResultSet lastInsertId = connection.prepareStatement("SELECT LAST_INSERT_ID()").executeQuery();
            lastInsertId.next();

            PreparedStatement ps = connection.prepareStatement(SELECT_BY_BRANDINGID);
            ps.setInt(1, lastInsertId.getInt("LAST_INSERT_ID()"));

            ResultSet result = ps.executeQuery();
            result.next();

            Branding createdBranding = new Branding(result);
            branding.setBrandingid(result.getInt("brandingid"));

            return createdBranding;
        } else {
            return null;
        }
    }

    public Branding query(int id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(SELECT_BY_BRANDINGID);
        ps.setInt(1, id);

        ResultSet result = ps.executeQuery();
        result.next();

        return new Branding(result);
    }

    public Boolean delete(int bookingid) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(DELETE_BY_BRANDINGID);
        ps.setInt(1, bookingid);

        int resultSet = ps.executeUpdate();
        return resultSet == 1;
    }

    public Branding update(int id, Branding room) throws SQLException {
        UpdateSql updateSql = new UpdateSql(connection, id, room);
        PreparedStatement updatePs = updateSql.getPreparedStatement();

        if(updatePs.executeUpdate() > 0) {
            PreparedStatement ps = connection.prepareStatement(SELECT_BY_BRANDINGID);
            ps.setInt(1, id);

            ResultSet result = ps.executeQuery();
            result.next();

            Branding createdBranding = new Branding(result);
            createdBranding.setBrandingid(result.getInt("brandingid"));

            return createdBranding;
        } else {
            return null;
        }
    }

    public List<Branding> queryBrandings() throws SQLException {
        List<Branding> listToReturn = new ArrayList<>();

        PreparedStatement ps = connection.prepareStatement(SELECT_ALL_BRANDINGS);

        ResultSet results = ps.executeQuery();
        while(results.next()) {
            listToReturn.add(new Branding(results));
        }

        return listToReturn;
    }

    public void resetDB() throws SQLException {
        PreparedStatement ps = connection.prepareStatement(DELETE_ALL_BRANDINGS);

        ps.executeUpdate();

        PreparedStatement resetPs = connection.prepareStatement("ALTER TABLE brandings ALTER COLUMN brandingid RESTART WITH 1");

        resetPs.execute();
    }
}
