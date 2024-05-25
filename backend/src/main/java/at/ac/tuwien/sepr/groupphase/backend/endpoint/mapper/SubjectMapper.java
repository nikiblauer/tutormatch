package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SubjectDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SubjectDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Subject;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper
public interface SubjectMapper {
    default SubjectDto subjectToDto(Subject subject) {
        SubjectDto dto = new SubjectDto();
        dto.setName(subject.getType() + " " + subject.getNumber() + " " + subject.getTitle());
        dto.setId(subject.getId());
        dto.setUrl(subject.getUrl());
        dto.setDescription(subject.getDescription());
        return dto;
    }

    default Page<SubjectDto> subjectListToDto(Page<Subject> subjects) {
        return subjects.map(this::subjectToDto);
    }

    default SubjectDetailDto subjectToSubjectDetailDto(Subject subject) {
        SubjectDetailDto subjectDetailDto = new SubjectDetailDto();

        subjectDetailDto.setId(subject.getId());
        subjectDetailDto.setType(subject.getType());
        subjectDetailDto.setDescription(subject.getDescription());
        subjectDetailDto.setNumber(subject.getNumber());
        subjectDetailDto.setTitle(subject.getTitle());
        subjectDetailDto.setSemester(subject.getSemester());
        subjectDetailDto.setUrl(subject.getUrl());

        return subjectDetailDto;
    }
}
