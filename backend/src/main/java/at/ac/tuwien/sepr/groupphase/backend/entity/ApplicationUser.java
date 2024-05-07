package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
public class ApplicationUser {

    @Setter
    @OneToOne(cascade = jakarta.persistence.CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "DETAILS_ID", referencedColumnName = "ID")
    private ContactDetails details;
    @Setter
    @Column(nullable = false, length = 255)
    private String password;
    @Setter
    @Column(nullable = false)
    private Boolean admin;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    private Long id;
    @Setter
    @Column(nullable = false, length = 255)
    private String firstname;
    @Setter
    @Column(nullable = false, length = 255)
    private String lastname;
    @Setter
    private Long matrNumber;

    public ApplicationUser() {
    }

    public ApplicationUser(String password, Boolean admin, String firstname, String lastname, Long matrNumber, ContactDetails details) {
        this.details = details;
        this.password = password;
        this.admin = admin;
        this.firstname = firstname;
        this.lastname = lastname;
        this.matrNumber = matrNumber;
    }
}
