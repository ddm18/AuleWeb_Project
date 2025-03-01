package it.univaq.f4i.iw.framework.data;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

import it.univaq.f4i.iw.ex.AuleWeb.data.model.Event;

/**
 *
 * @author Giuseppe Della Penna
 */
public class DAO {

    protected final DataLayer dataLayer;
    protected final Connection connection;

    public DAO(DataLayer d) {
        this.dataLayer = d;
        this.connection = d.getConnection();
    }

    protected DataLayer getDataLayer() {
        return dataLayer;
    }

    protected Connection getConnection() {
        return connection;
    }

    public void init() throws DataException {

    }

    public void destroy() throws DataException {

    }

}
