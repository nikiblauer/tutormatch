package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateApplicationUserDto {
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

    @Override
    public String toString() {
        return "ApplicationUserDto{"
            + "password='" + password + '\''
            + ", firstname='" + firstname + '\''
            + ", lastname='" + lastname + '\''
            + ", matrNumber=" + matrNumber
            + ", email='" + email + '\''
            + '}';
    }

    public CreateApplicationUserDto() {

    }

    /*public static final class ApplicationUserDtoBuilder {
        private String password;
        private String firstname;
        private String lastname;
        private Long matrNumber;
        private String email;
        private String telNr;

        private ApplicationUserDtoBuilder() {
        }

        public static CreateApplicationUserDto.ApplicationUserDtoBuilder aApplicationUserDto() {
            return new CreateApplicationUserDto.ApplicationUserDtoBuilder();
        }

        public CreateApplicationUserDto.ApplicationUserDtoBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public CreateApplicationUserDto.ApplicationUserDtoBuilder withFirstname(String firstname) {
            this.firstname = firstname;
            return this;
        }

        public CreateApplicationUserDto.ApplicationUserDtoBuilder withLastname(String lastname) {
            this.lastname = lastname;
            return this;
        }

        public CreateApplicationUserDto.ApplicationUserDtoBuilder withMatrNumber(Long matrNumber) {
            this.matrNumber = matrNumber;
            return this;
        }

        public CreateApplicationUserDto.ApplicationUserDtoBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public CreateApplicationUserDto.ApplicationUserDtoBuilder withTelNr(String telNr) {
            this.telNr = telNr;
            return this;
        }

        public CreateApplicationUserDto build() {
            CreateApplicationUserDto applicationUserDto = new CreateApplicationUserDto();
            applicationUserDto.setEmail(email);
            applicationUserDto.setFirstname(firstname);
            applicationUserDto.setLastname(lastname);
            applicationUserDto.setPassword(password);
            applicationUserDto.setMatrNumber(matrNumber);
            return applicationUserDto;
        }
    }*/
}
