package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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

    public StudentDto() {

    }

    public static final class ApplicationUserDtoBuilder {
        private String password;
        private String firstname;
        private String lastname;
        private Long matrNumber;
        private String email;
        private String telNr;

        private String street;

        private Integer areaCode;
        private String city;



        private ApplicationUserDtoBuilder() {
        }

        public static StudentDto.ApplicationUserDtoBuilder aApplicationUserDto() {
            return new StudentDto.ApplicationUserDtoBuilder();
        }

        public StudentDto.ApplicationUserDtoBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public StudentDto.ApplicationUserDtoBuilder withFirstname(String firstname) {
            this.firstname = firstname;
            return this;
        }

        public StudentDto.ApplicationUserDtoBuilder withLastname(String lastname) {
            this.lastname = lastname;
            return this;
        }

        public StudentDto.ApplicationUserDtoBuilder withMatrNumber(Long matrNumber) {
            this.matrNumber = matrNumber;
            return this;
        }

        public StudentDto.ApplicationUserDtoBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public StudentDto.ApplicationUserDtoBuilder withTelNr(String telNr) {
            this.telNr = telNr;
            return this;
        }

        public StudentDto.ApplicationUserDtoBuilder withStreet(String street) {
            this.street = street;
            return this;
        }

        public StudentDto.ApplicationUserDtoBuilder withAreaCode(Integer areaCode) {
            this.areaCode = areaCode;
            return this;
        }

        public StudentDto.ApplicationUserDtoBuilder withCity(String city) {
            this.city = city;
            return this;
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
    }
}
