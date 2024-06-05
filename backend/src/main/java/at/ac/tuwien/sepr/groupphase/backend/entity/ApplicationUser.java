package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
public class ApplicationUser {

    @OneToOne(cascade = jakarta.persistence.CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "DETAILS_ID", referencedColumnName = "ID")
    private ContactDetails details;
    @Column(nullable = false, length = 255)
    private String password;
    @Column(nullable = false)
    private Boolean admin;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 255)
    private String firstname;
    @Column(nullable = false, length = 255)
    private String lastname;
    private Long matrNumber;
    private Boolean verified;
    private Boolean visible = true;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<UserSubject> userSubjects;

    public ApplicationUser() {
    }

    public ApplicationUser(String password, Boolean admin, String firstname, String lastname, Long matrNumber, ContactDetails details, boolean verified) {
        this.details = details;
        this.password = password;
        this.admin = admin;
        this.firstname = firstname;
        this.lastname = lastname;
        this.matrNumber = matrNumber;
        this.verified = verified;
    }
}
