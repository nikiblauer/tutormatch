package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 255)
    @Getter
    @Setter
    private String street;

    @Getter
    @Setter
    private Integer areaCode;

    @Column(length = 255)
    @Getter
    @Setter
    private String city;

    public Address() {
    }

    public Address(String street, Integer areaCode, String city) {
        this.street = street;
        this.areaCode = areaCode;
        this.city = city;
    }

}
