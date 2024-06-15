package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ReportDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Report;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface ReportMapper {
    default List<ReportDto> reportToReportDto(List<Report> report){
        List<ReportDto> list = new ArrayList<>();
        for (Report value : report) {
            ReportDto a = new ReportDto();
            a.setLastName_Reported(value.getReportedUser().getLastname());
            a.setFirstname_Reported(value.getReportedUser().getFirstname());
            a.setFirstname_Reporter(value.getReporter().getFirstname());
            a.setLastname_Reporter(value.getReporter().getLastname());
            a.setReason(value.getReason());
            a.setId(value.getId());
            if (value.getReportedFeedback() != null) {
                a.setFeedback(value.getReportedFeedback().getFeedback());
            } else {
                a.setFeedback("");
            }
            list.add(a);
        }
        return list;
    }
}
