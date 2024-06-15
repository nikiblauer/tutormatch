package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import lombok.Data;

@Data
public class ReportDto {
    String Firstname_Reported;
    String LastName_Reported;
    String reason;
    Long id;
    String Firstname_Reporter;
    String Lastname_Reporter;
    String Feedback;

}
