package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
public class ContactDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 255)
    @Getter
    @Setter
    private String telNr;

    @Column(nullable = false, length = 255)
    @Getter
    @Setter
    private String email;

    public ContactDetails() {

    }

    public ContactDetails(String telNr, String email) {
        this.telNr = telNr;
        this.email = email;
    }
}
