package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ImportStatusDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.TissClientException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

public interface ImportService {

    /**
     * Retrieves the last import status from the repository and maps it to a DTO.
     *
     * @return an {@link ImportStatusDto} representing the last import status.
     */
    ImportStatusDto getLastImportStatus();

    /**
     * Starts the Tiss import process.
     * can be cancelled with cancelImport method.
     *
     * @param importId the import ID
     */
    void startImport(String importId) throws InterruptedException, TissClientException, ValidationException;

    /**
     * Starts the import process asynchronously.
     *
     * @param importId the import ID.
     * @throws InterruptedException if the import process is interrupted.
     * @throws TissClientException  if an error occurs while fetching data from Tiss.
     */
    void startImportAsync(String importId) throws InterruptedException, TissClientException, ValidationException;


    /**
     * Retrieves the status of a Tiss import process.
     *
     * @param importId the import ID
     * @return the import status DTO
     */
    ImportStatusDto getImportStatus(String importId);

    /**
     * Cancels the Tiss import process.
     *
     * @param importId the import ID
     */
    void cancelImport(String importId);
}
