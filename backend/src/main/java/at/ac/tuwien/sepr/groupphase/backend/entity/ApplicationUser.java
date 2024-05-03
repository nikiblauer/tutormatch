package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

//TODO: replace this class with a correct ApplicationUser Entity implementation
@Entity
@Table
public class ApplicationUser {

    @OneToOne
    @PrimaryKeyJoinColumn
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
    @Column(nullable = false)
    private Long matrNumber;

    public ApplicationUser() {
    }

    public ApplicationUser(String email, String password, Boolean admin) {
        this.details = new ContactDetails();
        this.details.email = email;
        this.password = password;
        this.admin = admin;
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
}
