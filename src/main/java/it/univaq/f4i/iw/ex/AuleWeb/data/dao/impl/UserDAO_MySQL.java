package it.univaq.f4i.iw.ex.AuleWeb.data.dao.impl;

import it.univaq.f4i.iw.ex.AuleWeb.data.dao.UserDAO;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.User;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.impl.proxy.UserProxy;
import it.univaq.f4i.iw.framework.data.DAO;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.data.DataItemProxy;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import it.univaq.f4i.iw.framework.data.DataLayer;
import it.univaq.f4i.iw.framework.data.OptimisticLockException;

public class UserDAO_MySQL extends DAO implements UserDAO {

    private PreparedStatement sUserByID, sUserByName, iUser, uUser, sUsers, dUser;

    public UserDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            // Precompile all queries used in this class
            sUserByID = connection.prepareStatement("SELECT * FROM users WHERE ID = ?");
            sUserByName = connection.prepareStatement("SELECT ID FROM users WHERE username = ?");
            sUsers = connection.prepareStatement("SELECT * FROM users");
            iUser = connection.prepareStatement(
                "INSERT INTO users (username, password) VALUES(?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            uUser = connection.prepareStatement(
                "UPDATE users SET username = ?, password = ?, version = ? WHERE ID = ? AND version = ?"
            );
            dUser = connection.prepareStatement("DELETE FROM users WHERE ID = ?");
        } catch (SQLException ex) {
            throw new DataException("Error initializing UserDAO", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        // Close prepared statements as part of cleanup
        try {
            if (sUserByID != null) sUserByID.close();
            if (sUserByName != null) sUserByName.close();
            if (sUsers != null) sUsers.close();
            if (iUser != null) iUser.close();
            if (uUser != null) uUser.close();
            if (dUser != null) dUser.close();
        } catch (SQLException ex) {
            // Log and suppress errors during destroy
        }
        super.destroy();
    }

    @Override
    public User createUser() {
        return new UserProxy(getDataLayer());
    }

    private UserProxy createUser(ResultSet rs) throws DataException {
        try {
            UserProxy user = (UserProxy) createUser();
            user.setKey(rs.getInt("ID"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setVersion(rs.getLong("version"));
            return user;
        } catch (SQLException ex) {
            throw new DataException("Unable to create User object from ResultSet", ex);
        }
    }

    @Override
    public User getUser(int userKey) throws DataException {
        User user = null;
        if (dataLayer.getCache().has(User.class, userKey)) {
            user = dataLayer.getCache().get(User.class, userKey);
        } else {
            try {
                sUserByID.setInt(1, userKey);
                try (ResultSet rs = sUserByID.executeQuery()) {
                    if (rs.next()) {
                        user = createUser(rs);
                        dataLayer.getCache().add(User.class, user);
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Unable to load User by ID", ex);
            }
        }
        return user;
    }

    @Override
    public User getUserByName(String username) throws DataException {
        try {
            sUserByName.setString(1, username);
            try (ResultSet rs = sUserByName.executeQuery()) {
                if (rs.next()) {
                    return getUser(rs.getInt("ID"));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to find User by username", ex);
        }
        return null;
    }

    @Override
    public List<User> getUsers() throws DataException {
        List<User> users = new ArrayList<>();
        try (ResultSet rs = sUsers.executeQuery()) {
            while (rs.next()) {
                users.add(createUser(rs));
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load users", ex);
        }
        return users;
    }

    @Override
    public void deleteUser(int userKey) throws DataException {
        try {
            dUser.setInt(1, userKey);
            dUser.executeUpdate();
        } catch (SQLException ex) {
            throw new DataException("Unable to delete User", ex);
        }
    }

    @Override
    public void storeUser(User user) throws DataException {
        try {
            if (user.getKey() != null && user.getKey() > 0) { // Update
                if (user instanceof DataItemProxy && !((DataItemProxy) user).isModified()) {
                    return;
                }

                uUser.setString(1, user.getUsername());
                uUser.setString(2, user.getPassword());

                long currentVersion = user.getVersion();
                long nextVersion = currentVersion + 1;

                uUser.setLong(3, nextVersion);
                uUser.setInt(4, user.getKey());
                uUser.setLong(5, currentVersion);

                if (uUser.executeUpdate() == 0) {
                    throw new OptimisticLockException(user);
                } else {
                    user.setVersion(nextVersion);
                }
            } else { // Insert
                iUser.setString(1, user.getUsername());
                iUser.setString(2, user.getPassword());

                if (iUser.executeUpdate() == 1) {
                    try (ResultSet keys = iUser.getGeneratedKeys()) {
                        if (keys.next()) {
                            int key = keys.getInt(1);
                            user.setKey(key);
                            dataLayer.getCache().add(User.class, user);
                        }
                    }
                }
            }

            if (user instanceof DataItemProxy) {
                ((DataItemProxy) user).setModified(false);
            }
        } catch (SQLException | OptimisticLockException ex) {
            throw new DataException("Unable to store User", ex);
        }
    }


}
