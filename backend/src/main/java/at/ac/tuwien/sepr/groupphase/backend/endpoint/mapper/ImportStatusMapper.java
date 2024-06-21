package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ImportStatusDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ImportStatus;
import org.mapstruct.Mapper;

/**
 * Mapper class to convert between ImportStatus entity and ImportStatusDTO.
 */
@Mapper
public interface ImportStatusMapper {

    /**
     * Converts an ImportStatus entity to an ImportStatusDto.
     *
     * @param importStatus the import status entity
     * @return the import status DTO
     */
    default ImportStatusDto importStatusToDto(ImportStatus importStatus){
        if (importStatus == null)
            return null;
        var dto = new ImportStatusDto();
        dto.setImportId(importStatus.getImportId());
        dto.setStatus(importStatus.getStatus().toString());
        dto.setImportDate(importStatus.getImportDate());
        return dto;
    }

    /**
     * Converts an ImportStatus entity to an ImportStatusDto.
     *
     * @param importStatus the import status entity
     * @param progress the import progress as per cent
     * @return the import status DTO
     */
    default ImportStatusDto importStatusToDto(ImportStatus importStatus, int progress){
        var dto = importStatusToDto(importStatus);
        dto.setProgress(progress);
        return dto;
    }
}
