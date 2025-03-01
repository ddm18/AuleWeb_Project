package it.univaq.f4i.iw.ex.AuleWeb.data.dao;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.ClassroomGroup;
import it.univaq.f4i.iw.framework.data.DataException;
import java.util.List;

public interface ClassroomGroupDAO {

    // Retrieve a single ClassroomGroup by its ID
    ClassroomGroup getClassroomGroup(int groupID) throws DataException;

    // Retrieve a list of all ClassroomGroups
    List<ClassroomGroup> getClassroomGroups() throws DataException;

    // Factory method to create an empty ClassroomGroup object
    ClassroomGroup createClassroomGroup() throws DataException;

    // CRUD operations for storing and deleting
    void storeClassroomGroup(ClassroomGroup group) throws DataException;

    void deleteClassroomGroup(int groupID) throws DataException;

	ClassroomGroup getClassroomGroupByName(String group_name) throws DataException;
}
