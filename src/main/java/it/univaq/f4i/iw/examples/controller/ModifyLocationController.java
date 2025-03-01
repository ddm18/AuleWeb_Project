package it.univaq.f4i.iw.examples.controller;

import it.univaq.f4i.iw.ex.AuleWeb.data.model.Location;
import it.univaq.f4i.iw.examples.application.ApplicationBaseController;
import it.univaq.f4i.iw.examples.application.ApplicationDataLayer;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.view.TemplateManagerException;
import it.univaq.f4i.iw.framework.view.TemplateResult;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ModifyLocationController extends ApplicationBaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response) throws IOException, TemplateManagerException, DataException {
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");
        List<Location> locations = dl.getLocationDAO().getLocations();
        request.setAttribute("locations", locations);
        request.setAttribute("page_title", "Manage Locations");

        TemplateResult result = new TemplateResult(getServletContext());
        result.activate("modify_location.ftl.html", request, response);
    }

    private void action_add(HttpServletRequest request, HttpServletResponse response) throws IOException, DataException {
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");

        try {
            // Gather parameters for the new location
            String place = request.getParameter("place");
            String building = request.getParameter("building");
            String floor = request.getParameter("floor");

            // Validate input
            if (place == null || place.trim().isEmpty() || building == null || building.trim().isEmpty() || floor == null || floor.trim().isEmpty()) {
                throw new DataException("Place, building, and floor must not be empty.");
            }

            // Check if the location already exists
            Location existingLocation = dl.getLocationDAO().getLocationByDetails(place, building, floor);
            if (existingLocation != null) {
                throw new DataException("A location with the same place, building, and floor already exists.");
            }

            // Create and store the new location
            Location newLocation = dl.getLocationDAO().createLocation();
            newLocation.setPlace(place);
            newLocation.setBuilding(building);
            newLocation.setFloor(floor);

            dl.getLocationDAO().storeLocation(newLocation);

            // Redirect to refresh the list of locations
            response.sendRedirect("modify_location");
        } catch (DataException ex) {
            handleError("Error: " + ex.getMessage(), request, response);
        } catch (Exception ex) {
            handleError("An unexpected error occurred: " + ex.getMessage(), request, response);
        }
    }


    private void action_modify(HttpServletRequest request, HttpServletResponse response) throws IOException, DataException {
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");

        try {
            // Gather and normalize parameters
            int locationKey = Integer.parseInt(request.getParameter("location_key"));
            String newPlace = request.getParameter("place").trim();
            String newBuilding = request.getParameter("building").trim();
            String newFloor = request.getParameter("floor").trim();

            // Validate input
            if (newPlace.isEmpty() || newBuilding.isEmpty() || newFloor.isEmpty()) {
                throw new DataException("All fields (place, building, and floor) are required and cannot be empty.");
            }

            // Check for duplicate location
            Location existingLocation = dl.getLocationDAO().getLocationByDetails(newPlace, newBuilding, newFloor);
            if (existingLocation != null && existingLocation.getKey() != locationKey) {
                throw new DataException(
                    "A location with the same place, building, and floor already exists. Please use unique values."
                );
            }

            // Retrieve the existing location
            Location location = dl.getLocationDAO().getLocation(locationKey);
            if (location == null) {
                throw new DataException("The location you are trying to modify does not exist.");
            }

            // Update and save location
            location.setPlace(newPlace);
            location.setBuilding(newBuilding);
            location.setFloor(newFloor);

            dl.getLocationDAO().storeLocation(location);

            // Redirect to the modify page after successful update
            response.sendRedirect("modify_location");

        } catch (DataException ex) {
            // Handle validation and database-specific errors
            handleError("Location modification error: " + ex.getMessage(), request, response);
        } catch (NumberFormatException ex) {
            // Handle invalid numeric input
            handleError("Invalid input for location key. Please provide a valid number.", request, response);
        } catch (Exception ex) {
            // Catch unexpected exceptions
            handleError("An unexpected error occurred: " + ex.getMessage(), request, response);
        }
    }



    private void action_delete(HttpServletRequest request, HttpServletResponse response) throws IOException, DataException {
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");
        int locationKey = Integer.parseInt(request.getParameter("location_key"));

        dl.getLocationDAO().deleteLocation(locationKey);

        response.sendRedirect("modify_location");
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String formInput = request.getParameter("form_input");

        if (formInput == null) {
            action_default(request, response);
        } else {
            switch (formInput) {
                case "add_location":
                    action_add(request, response);
                    break;
                case "modify_location":
                    action_modify(request, response);
                    break;
                case "delete_location":
                    action_delete(request, response);
                    break;
                default:
                    action_default(request, response);
            }
        }
    }
}
