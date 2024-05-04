package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

//TODO: replace this class with a correct ApplicationUser Entity implementation
@Entity
@Table
public class ApplicationUser {

    @OneToOne(cascade = jakarta.persistence.CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "DETAILS_ID", referencedColumnName = "ID")
    private ContactDetails details;
    @Column(nullable = false, length = 255)
    private String password;
    @Column(nullable = false, name = "IS_ADMIN")
    private Boolean admin;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 255)
    private String name;
    private Long matrNumber;

    public ApplicationUser() {
    }

    public ApplicationUser(String email, String password, Boolean admin, String name, String telNr, Long matrNumber) {
        //this.details.email = email;
        this.password = password;
        this.admin = admin;
        this.name = name;
        this.matrNumber = matrNumber;
        //this.details.telNr = telNr;
    }

    public String getEmail() {
        return details.email;
    }

    public void setEmail(String email) {
        this.details.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public ContactDetails getDetails() {
        return this.details;
    }

    public void setDetails(ContactDetails details) {
        this.details = details;
    }
}
