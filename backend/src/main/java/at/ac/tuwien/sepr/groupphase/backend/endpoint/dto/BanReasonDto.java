package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BanReasonDto {
    @NotBlank
    @Size(max = 1000, message = "Reason too long")
    private String reason;
}

