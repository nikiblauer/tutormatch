package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.RatingDto;

public interface RatingService {
    /**
     * gets a rating for a student.
     *
     * @param student the student of whom the rating is
     * @return the current rating of a student as double from 0-5
     */
    float[] getRatingOfStudent(long student);

    /**
     * updates an existing rating of one student to another. If none exists it creates a new one.
     *
     * @param ratingDto contains both students and the rating
     * @throws Exception if both students are the same.
     */
    void updatedRating(RatingDto ratingDto, long rater) throws Exception;

    /**
     * gets the value of rating of which rater rated rated.
     *
     * @param rated the student that rated
     * @param rater the student that was rated
     * @return the rating of if none exits -1
     */
    float getRatingFromStudent(long rated, long rater);
}
