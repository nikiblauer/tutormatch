package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ReportDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Report;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface ReportMapper {
    default List<ReportDto> reportToReportDto(List<Report> report) {
        List<ReportDto> list = new ArrayList<>();
        for (Report value : report) {
            ReportDto a = new ReportDto();
            a.setLastNameReported(value.getReportedUser().getLastname());
            a.setFirstnameReported(value.getReportedUser().getFirstname());
            a.setFirstnameReporter(value.getReporter().getFirstname());
            a.setLastnameReporter(value.getReporter().getLastname());
            a.setReason(value.getReason());
            a.setId(value.getId());
            if (value.getReportedFeedback() != null) {
                a.setFeedback(value.getReportedFeedback().getFeedback());
            } else {
                a.setFeedback("");
            }
            if (value.getChatRoomId() != null) {
                a.setChatRoomId(value.getChatRoomId());
            } else {
                a.setChatRoomId("");
            }
            a.setReporterId(value.getReporter().getId());
            a.setReportedId(value.getReportedUser().getId());
            list.add(a);
        }
        return list;
    }
}
