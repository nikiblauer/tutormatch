package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateApplicationUserDto {

    public Long id;

    @NotBlank(message = "Firstname is mandatory")
    public String firstname;

    @NotBlank(message = "Lastname is mandatory")
    public String lastname;

    //matrNumber should not be updated
    public Long matrNumber;

    //email should not be updated
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    public String email;

    public String telNr;

    public String street;

    public Integer areaCode;

    public String city;

    @Override
    public String toString() {
        return "ApplicationUserDto{"
            + ", firstname='" + firstname + '\''
            + ", lastname='" + lastname + '\''
            + ", matrNumber=" + matrNumber
            + ", email='" + email + '\''
            + ", telNr='" + telNr + '\''
            + ", street='" + street + '\''
            + ", areaCode='" + areaCode + '\''
            + ", city='" + city + '\''
            + '}';
    }

    public UpdateApplicationUserDto() {

    }
}
