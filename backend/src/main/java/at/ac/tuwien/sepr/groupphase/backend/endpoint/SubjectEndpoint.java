package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SubjectDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SubjectDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.SubjectMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Subject;
import at.ac.tuwien.sepr.groupphase.backend.service.SubjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/subject")
@Tag(name = "Subject Endpoint")
public class SubjectEndpoint {
    private final SubjectService subjectService;
    private final SubjectMapper mapper;

    public SubjectEndpoint(SubjectService subjectService, SubjectMapper mapper) {
        this.subjectService = subjectService;
        this.mapper = mapper;
    }

    @Operation(
        description = "Returns all subjects, that meet certain criteria in pages. The criteria can be empty.",
        summary = "Get Subjects")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping()
    public Page<SubjectDto> getSubjectsByQuery(@RequestParam(name = "q", required = false) String searchParam, Pageable pageable) {
        //default page size is 20
        Page<Subject> subjects = subjectService.findSubjectsBySearchParam(searchParam, pageable);
        return mapper.subjectListToDto(subjects);
    }

    @Operation(
        description = "Get details of a certain subject.",
        summary = "Get subject details.")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/{id}")
    public SubjectDetailDto getSubject(@PathVariable("id") Long id) {
        Subject subject = subjectService.getSubjectById(id);
        return mapper.subjectToSubjectDetailDto(subject);
    }
}
