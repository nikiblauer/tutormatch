package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.FeedbackCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.FeedbackDto;
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
     * @param ratingDto contains rated student and the rating
     * @param ratingUserId the user that rates
     * @throws Exception if both students are the same.
     */
    void updatedRating(RatingDto ratingDto, long ratingUserId) throws Exception;

    /**
     * gets the value of rating of which ratingUserId rated ratedUserId.
     *
     * @param ratedUserId the student that rated
     * @param ratingUserId the student that was rated
     * @return the rating of if none exits -1
     */
    float getRatingFromStudent(long ratedUserId, long ratingUserId);

    /**
     * gets all feedback for a student.
     *
     * @param student the student for whom the feedback is
     * @return the current feedback for the student
     */
    FeedbackDto[] getFeedbackOfStudent(long student);

    /**
     * gets all feedback by a student.
     *
     * @param student the student who wrote the feedback
     * @return the feedback written by the student
     */
    FeedbackDto[] getFeedbackByStudent(long student);

    FeedbackDto[] getFeedbackByAndForStudent(long id1, long id2);

    /**
     * returns true if a chat between two users exists.
     *
     * @param studentId1 the first participant
     * @param studentId2 the second participant
     * @return true if at least one message between the students exists, else false
     */
    boolean chatExists(long studentId1, long studentId2);

    /**
     * adds a new feedback for a user.
     *
     * @param feedbackCreateDto contains rated students and the rating
     * @param ratingUserId the user that rates
     * @throws Exception if both students are the same, or no chat between those users exists
     */
    void giveFeedback(FeedbackCreateDto feedbackCreateDto, long ratingUserId) throws Exception;

    /**
     * deletes all feedback written by student.
     *
     * @param student the student whose feedback should be deleted
     */
    void deleteFeedbackOfStudent(long student);

    /**
     * deletes a feedback written by student by id.
     *
     * @param id the id of the feedback to be deleted
     */
    void deleteFeedbackByIdAdmin(long id);

    /**
     * deletes a feedback written by student by id.
     *
     * @param deleteId the id of the feedback to be deleted
     * @param requestUserId the id of the feedback to be deleted
     * @throws Exception if student is neither giver nor receiver of feedback with given deleteId
     */
    void deleteFeedbackByIdStudent(long deleteId, long requestUserId) throws Exception;
}
