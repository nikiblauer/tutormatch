package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSubject {
    @EmbeddedId
    @GeneratedValue
    @Id
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
