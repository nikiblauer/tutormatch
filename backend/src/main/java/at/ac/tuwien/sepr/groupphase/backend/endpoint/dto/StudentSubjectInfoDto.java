package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class StudentSubjectInfoDto {

    private String firstname;

    private String lastname;

    private String email;

    private String telNr;

    public String street;

    public Integer areaCode;

    public String city;

    private String[] tutorSubjects;

    private String[] traineeSubjects;
}
