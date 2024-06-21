package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ImportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository interface for accessing ImportStatus of Tiss data.
 */
public interface ImportStatusRepository extends JpaRepository<ImportStatus, Long> {

    /**
     * Finds the import status by the given import ID.
     *
     * @param importId the import ID
     * @return the import status
     */
    ImportStatus findByImportId(String importId);

    /**
     * Finds the top import status ordered by completion date descending.
     *
     * @return the ImportStatus entity or null if not found
     */
    @Query("SELECT s FROM ImportStatus s ORDER BY s.importDate DESC LIMIT 1")

    ImportStatus findLastImportStatus();
}


