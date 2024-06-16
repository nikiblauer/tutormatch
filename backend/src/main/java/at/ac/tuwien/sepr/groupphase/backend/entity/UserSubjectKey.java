package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class UserSubjectKey implements Serializable {
    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "SUBJECT_ID")
    private Long subjectId;

}
