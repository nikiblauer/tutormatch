package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class ContactDetails {
    @Id
    @OneToOne
    @JoinColumn(name = "USER_ID")
    private ApplicationUser user;

    @Column(name = "TEL_NR", length = 255)
    private String telNr;

    @Column(nullable = false, length = 255)
    public String email;
}
