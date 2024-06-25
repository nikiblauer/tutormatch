package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import lombok.Data;

@Data
public class ReportDto {
    String firstnameReported;
    String lastNameReported;
    Long reportedId;
    Long reporterId;
    String reason;
    Long id;
    String firstnameReporter;
    String lastnameReporter;
    String feedback;
    String chatRoomId;
}
