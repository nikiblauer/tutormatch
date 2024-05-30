package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentDto {

    public Long id;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    public String password;

    @NotBlank(message = "Firstname is mandatory")
    public String firstname;

    @NotBlank(message = "Lastname is mandatory")
    public String lastname;

    @NotNull(message = "MatrNumber is mandatory")
    public Long matrNumber;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    public String email;

    public String telNr;

    public String street;

    public Integer areaCode;

    public String city;

    public StudentDto() {
    }

    public StudentDto build() {
        StudentDto studentDto = new StudentDto();
        studentDto.setEmail(email);
        studentDto.setFirstname(firstname);
        studentDto.setLastname(lastname);
        studentDto.setPassword(password);
        studentDto.setMatrNumber(matrNumber);
        studentDto.setStreet(street);
        studentDto.setAreaCode(areaCode);
        studentDto.setCity(city);
        return studentDto;
    }

    @Override
    public String toString() {
        return "ApplicationUserDto{"
            + "password='" + password + '\''
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
}
