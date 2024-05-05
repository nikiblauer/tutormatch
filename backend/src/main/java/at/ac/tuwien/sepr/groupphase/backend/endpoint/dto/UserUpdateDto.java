package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

//TODO add subjects to this DTO, subjectsDTO ?
public record UserUpdateDto(
    @NotNull Long id,
    @NotNull @Size(min = 1, max = 100) String name,
    @NotNull @Email String email,
    @NotNull @Size(min = 8, max = 100) String password
) {
}
