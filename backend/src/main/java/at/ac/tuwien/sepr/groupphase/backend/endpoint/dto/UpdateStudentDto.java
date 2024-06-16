package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStudentDto {

    public Long id;

    @NotBlank(message = "Firstname is mandatory")
    public String firstname;

    @NotBlank(message = "Lastname is mandatory")
    public String lastname;

    public String telNr;

    public String street;

    public Integer areaCode;

    public String city;

    @Override
    public String toString() {
        return "ApplicationUserDto{"
            + ", firstname='" + firstname + '\''
            + ", lastname='" + lastname + '\''
            + ", telNr='" + telNr + '\''
            + ", street='" + street + '\''
            + ", areaCode='" + areaCode + '\''
            + ", city='" + city + '\''
            + '}';
    }

    public UpdateStudentDto() {

    }
}
