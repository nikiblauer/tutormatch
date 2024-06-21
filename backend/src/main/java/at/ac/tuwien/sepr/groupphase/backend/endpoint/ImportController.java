package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ImportStatusDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ImportStatusRequestDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.TissClientException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.ImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/import")
@Tag(name = "Import Controller", description = "Operations related to tiss subject data import")
public class ImportController {
    private final ImportService importService;
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public ImportController(ImportService importService) {
        this.importService = importService;
    }

    @Operation(
        summary = "Starts a new import process",
        description = "Creates a new import process and returns an import ID."
    )
    @Secured("ROLE_ADMIN")
    @GetMapping("/start")
    @ResponseStatus(HttpStatus.OK)
    public ImportStatusRequestDto startImport() throws ValidationException, TissClientException, InterruptedException {
        var lastImport = importService.getLastImportStatus();
        if (lastImport != null && lastImport.getStatus().toLowerCase().equals("running")) {
            throw new ValidationException("An other import with ID " + lastImport.getImportId() + " is already running. Please wait for it to complete.");
        }
        var importId = UUID.randomUUID().toString();
        importService.startImportAsync(importId);
        return new ImportStatusRequestDto(importId);
    }

    @Operation(
        summary = "Cancels an ongoing import process",
        description = "Cancels an import process given its ID."
    )
    @PostMapping("/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelImport(@RequestBody ImportStatusRequestDto importStatus) {
        importService.cancelImport(importStatus.getImportId());
    }

    @Operation(
        summary = "Gets the status of an import process",
        description = "Retrieves the current status of an import process."
    )
    @GetMapping("/{importId}/status")
    @ResponseStatus(HttpStatus.OK)
    public ImportStatusDto getImportStatus(@PathVariable("importId") String importId) {
        return importService.getImportStatus(importId);
    }

    @Operation(
        summary = "Gets the last import process",
        description = "Retrieves the status of the last import process."
    )
    @GetMapping("/status")
    @ResponseStatus(HttpStatus.OK)
    public ImportStatusDto getLastImport() {
        return importService.getLastImportStatus();
    }

}
