package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
public class UserLoginDto {

    @NotNull(message = "Email must not be null")
    @Email
    private String email;

    @NotNull(message = "Password must not be null")
    private String password;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserLoginDto userLoginDto)) {
            return false;
        }
        return Objects.equals(email, userLoginDto.email)
            && Objects.equals(password, userLoginDto.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password);
    }

    @Override
    public String toString() {
        return "UserLoginDto{"
            + "email='" + email + '\''
            + ", password='" + password + '\''
            + '}';
    }
}
