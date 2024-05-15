package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ApplicationUserDetailDto {
    @NotBlank(message = "Firstname is mandatory")
    private String firstname;

    @NotBlank(message = "Lastname is mandatory")
    private String lastname;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    private String telNr;

    public String street;

    public Integer areaCode;

    public String city;

    @Override
    public String toString() {
        return "ApplicationUserDetailDto{"
            + "firstname='" + firstname + '\''
            + ", lastname='" + lastname + '\''
            + ", email='" + email + '\''
            + ", telNr='" + telNr + '\''
            + ", address='" + street + " " + areaCode + " " + city + '\''
            + '}';
    }
}
