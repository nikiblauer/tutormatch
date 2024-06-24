package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStudentDto {

    public Long id;

    @NotBlank(message = "Firstname is mandatory")
    @Size(max = 255)
    public String firstname;

    @NotBlank(message = "Lastname is mandatory")
    @Size(max = 255)
    public String lastname;

    public String telNr;

    @Size(max = 255)
    public String street;

    public Integer areaCode;

    @Size(max = 255)
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
