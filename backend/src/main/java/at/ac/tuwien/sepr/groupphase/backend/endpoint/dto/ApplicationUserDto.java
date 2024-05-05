package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ApplicationUserDto {

    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    public String password;

    @NotBlank(message = "Name is mandatory")
    public String name;

    @NotNull(message = "MatrNumber is mandatory")
    public Long matrNumber;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    public String email;

    @NotBlank(message = "TelNr is mandatory")
    public String telNr;

    public static ApplicationUserDto toDto(ApplicationUser applicationUser) {
        ApplicationUserDto dto = new ApplicationUserDto();
        dto.password = applicationUser.getPassword();
        dto.name = applicationUser.getName();
        dto.matrNumber = applicationUser.getMatrNumber();
        dto.email = applicationUser.getDetails().getEmail();
        dto.telNr = applicationUser.getDetails().getTelNr();
        return dto;
    }
}
