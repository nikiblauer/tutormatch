package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Getter
@Setter
public class ApplicationUserDto {

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

    @NotBlank(message = "TelNr is mandatory")
    public String telNr;


    @Override
    public String toString() {
        return "ApplicationUserDto{"
            + "password='" + password + '\''
            + ", firstname='" + firstname + '\''
            + ", lastname='" + lastname + '\''
            + ", matrNumber=" + matrNumber
            + ", email='" + email + '\''
            + ", telNr='" + telNr + '\''
            + '}';
    }

    public ApplicationUserDto() {

    }

    public static final class ApplicationUserDtoBuilder {
        private String password;
        private String firstname;
        private String lastname;
        private Long matrNumber;
        private String email;
        private String telNr;

        private ApplicationUserDtoBuilder() {
        }

        public static ApplicationUserDto.ApplicationUserDtoBuilder aApplicationUserDto() {
            return new ApplicationUserDto.ApplicationUserDtoBuilder();
        }

        public ApplicationUserDto.ApplicationUserDtoBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public ApplicationUserDto.ApplicationUserDtoBuilder withFirstname(String firstname) {
            this.firstname = firstname;
            return this;
        }

        public ApplicationUserDto.ApplicationUserDtoBuilder withLastname(String lastname) {
            this.lastname = lastname;
            return this;
        }

        public ApplicationUserDto.ApplicationUserDtoBuilder withMatrNumber(Long matrNumber) {
            this.matrNumber = matrNumber;
            return this;
        }

        public ApplicationUserDto.ApplicationUserDtoBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public ApplicationUserDto.ApplicationUserDtoBuilder withTelNr(String telNr) {
            this.telNr = telNr;
            return this;
        }

        public ApplicationUserDto build() {
            ApplicationUserDto applicationUserDto = new ApplicationUserDto();
            applicationUserDto.setEmail(email);
            applicationUserDto.setFirstname(firstname);
            applicationUserDto.setLastname(lastname);
            applicationUserDto.setPassword(password);
            applicationUserDto.setMatrNumber(matrNumber);
            return applicationUserDto;
        }
    }
}
