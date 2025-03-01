package it.univaq.f4i.iw.examples.controller;

import it.univaq.f4i.iw.ex.AuleWeb.data.model.User;
import it.univaq.f4i.iw.examples.application.ApplicationBaseController;
import it.univaq.f4i.iw.examples.application.ApplicationDataLayer;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.security.SecurityHelpers;
import it.univaq.f4i.iw.framework.view.TemplateManagerException;
import it.univaq.f4i.iw.framework.view.TemplateResult;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ModifyUserController extends ApplicationBaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response) throws IOException, TemplateManagerException, DataException {
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");
        List<User> users = dl.getUserDAO().getUsers(); // Assume this method exists in UserDAO
        request.setAttribute("users", users);
        request.setAttribute("page_title", "Manage Users");

        TemplateResult result = new TemplateResult(getServletContext());
        result.activate("modify_user.ftl.html", request, response);
    }

    private void action_add(HttpServletRequest request, HttpServletResponse response) throws IOException, DataException, NoSuchAlgorithmException, InvalidKeySpecException {
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");

        try {
            // Gather parameters for the new user
            String username = request.getParameter("username");
            String password = request.getParameter("password");

            // Validate inputs
            if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                throw new DataException("Username and password cannot be empty.");
            }

            // Check if a user with the same username already exists
            if (dl.getUserDAO().getUserByName(username) != null) {
                throw new DataException("Cannot add user: duplicate username.");
            }

            // Create and store the new user
            User newUser = dl.getUserDAO().createUser();
            newUser.setUsername(username);

            // Securely hash the password before saving
            newUser.setPassword(SecurityHelpers.getPasswordHashPBKDF2(password));

            dl.getUserDAO().storeUser(newUser);

            // Redirect to refresh the list of users
            response.sendRedirect("modify_user");
        } catch (DataException ex) {
            handleError("Error: " + ex.getMessage(), request, response);
        } catch (Exception ex) {
            handleError("An unexpected error occurred: " + ex.getMessage(), request, response);
        }
    }


    private void action_modify(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, DataException, NoSuchAlgorithmException, InvalidKeySpecException {
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");

        try {
            // Gather parameters for modifying the user
            int userKey = Integer.parseInt(request.getParameter("user_key"));
            String newUsername = request.getParameter("username");
            String newPassword = request.getParameter("password");

            // Validate inputs
            if (newUsername == null || newUsername.trim().isEmpty() || newPassword == null || newPassword.trim().isEmpty()) {
                throw new DataException("Username and password cannot be empty.");
            }

            // Retrieve the existing user
            User user = dl.getUserDAO().getUser(userKey);
            if (user == null) {
                throw new DataException("User does not exist.");
            }

            // Check if a user with the new username already exists (excluding the current user)
            User existingUser = dl.getUserDAO().getUserByName(newUsername);
            if (existingUser != null && existingUser.getKey() != userKey) {
                throw new DataException("Cannot modify user: duplicate username.");
            }

            // Update the user details
            user.setUsername(newUsername);

            // Securely hash the new password before saving
            user.setPassword(SecurityHelpers.getPasswordHashPBKDF2(newPassword));

            // Store the updated user
            dl.getUserDAO().storeUser(user);

            // Redirect to refresh the list of users
            response.sendRedirect("modify_user");
        } catch (DataException ex) {
            handleError("Error: " + ex.getMessage(), request, response);
        } catch (Exception ex) {
            handleError("An unexpected error occurred: " + ex.getMessage(), request, response);
        }
    }


    private void action_delete(HttpServletRequest request, HttpServletResponse response) throws IOException, DataException {
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");
        int userKey = Integer.parseInt(request.getParameter("user_key"));

        dl.getUserDAO().deleteUser(userKey); // Assume this method exists in UserDAO

        response.sendRedirect("modify_user");
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String formInput = request.getParameter("form_input");

        if (formInput == null) {
            action_default(request, response);
        } else {
            switch (formInput) {
                case "add_user":
                    action_add(request, response);
                    break;
                case "modify_user":
                    action_modify(request, response);
                    break;
                case "delete_user":
                    action_delete(request, response);
                    break;
                default:
                    action_default(request, response);
            }
        }
    }
}
