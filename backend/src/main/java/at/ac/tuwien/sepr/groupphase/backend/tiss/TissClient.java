package at.ac.tuwien.sepr.groupphase.backend.tiss;

import at.ac.tuwien.sepr.groupphase.backend.entity.Subject;
import at.ac.tuwien.sepr.groupphase.backend.exception.TissClientException;

import java.util.List;

/**
 *  TissClient interface interacting with the TISS API.
 */
public interface TissClient {

    /**
     * Fetches course information for a specific course number and semester.
     *
     * @param number   the course number
     * @param semester the semester
     * @return the course information as a Subject object
     * @throws TissClientException if an error occurs during the request
     */
    Subject getCourseInfo(String number, String semester) throws TissClientException;

    /**
     * Fetches all courses for a specific organizational unit.
     *
     * @param orgUnit the organizational unit identifier
     * @return a list of courses as Subject objects
     * @throws TissClientException if an error occurs during the request
     */
    List<Subject> getOrgUnitCourses(String orgUnit) throws TissClientException;


    /**
     * Fetches all organizational units from the TISS API.
     *
     * @return a list of organizational unit identifiers
     * @throws TissClientException if an error occurs during the request
     */
    List<String> getOrgUnits() throws TissClientException;
}
