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

@Entity
public class ContactDetails {
    @Getter
    @Setter
    @OneToOne(cascade = jakarta.persistence.CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "ADDRESS_ID", referencedColumnName = "ID")
    private Address address;

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

    public ContactDetails(String telNr, String email, Address address) {
        this.telNr = telNr;
        this.email = email;
        this.address = address;
    }

}
