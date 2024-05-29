package at.ac.tuwien.sepr.groupphase.backend.service;

public interface RatingService {
    /**
     * gets a rating for a student.
     *
     * @param student the student of whom the rating is
     * @return the current rating of a student as double from 0-5
     */
    float getRatingOfStudent(long student);
}
