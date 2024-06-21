package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportStatusDto {
    private String importId;
    private String status;
    private int progress;

    //representing the date and time the import was initiated
    private Date importDate;
}
