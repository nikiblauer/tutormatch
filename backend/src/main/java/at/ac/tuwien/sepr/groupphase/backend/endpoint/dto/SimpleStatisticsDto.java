package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimpleStatisticsDto {

    private int registeredVerifiedUsers;

    private int registeredUnverifiedUsers;

    private double ratioOfferedNeededSubjects;

    private double openChatsPerUser;
}
