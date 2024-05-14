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

}
