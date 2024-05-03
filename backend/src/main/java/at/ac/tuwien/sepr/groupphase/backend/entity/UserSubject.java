package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;

@Entity
public class UserSubject {
    @EmbeddedId
    private UserSubjectKey id;
    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "USER_ID")
    private ApplicationUser user;
    @ManyToOne
    @MapsId("subjectId")
    @JoinColumn(name = "SUBJECT_ID")
    private Subject subject;
    @Column(nullable = false, length = 255)
    private String role;

}
