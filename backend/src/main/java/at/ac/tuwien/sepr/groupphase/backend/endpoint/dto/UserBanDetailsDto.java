package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class UserBanDetailsDto extends StudentBaseInfoDto {
    private Long id;
    private LocalDateTime banDate;
    private String reason;
}
