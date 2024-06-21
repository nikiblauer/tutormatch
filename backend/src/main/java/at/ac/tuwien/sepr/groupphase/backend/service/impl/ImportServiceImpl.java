package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ImportStatusDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ImportStatusMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ImportStatus;
import at.ac.tuwien.sepr.groupphase.backend.entity.Subject;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.TissClientException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ImportStatusRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.SubjectRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.ImportService;
import at.ac.tuwien.sepr.groupphase.backend.tiss.TissClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of the ImportService interface.
 * Handles the import of subjects from Tiss.
 */
@Service
public class ImportServiceImpl implements ImportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final SubjectRepository subjectRepository;
    private final TissClient tissClient;
    private final ImportStatusRepository importStatusRepository;
    private final ImportStatusMapper mapper;
    private final ConcurrentHashMap<String, Integer> progressMap;
    private final ConcurrentHashMap<String, Boolean> cancellationMap;
    private final PlatformTransactionManager transactionManager;



    public ImportServiceImpl(SubjectRepository subjectRepository, TissClient tissClient, ImportStatusRepository importStatusRepository, ImportStatusMapper mapper, PlatformTransactionManager transactionManager) {
        this.subjectRepository = subjectRepository;
        this.tissClient = tissClient;
        this.importStatusRepository = importStatusRepository;
        this.mapper = mapper;
        this.transactionManager = transactionManager;
        this.cancellationMap = new ConcurrentHashMap<>();
        this.progressMap = new ConcurrentHashMap<>();
    }


    /**
     * Retrieves the last import status from the repository and maps it to a DTO.
     *
     * @return an {@link ImportStatusDto} representing the last import status.
     */
    public ImportStatusDto getLastImportStatus() {
        var status = importStatusRepository.findLastImportStatus();
        return mapper.importStatusToDto(status, progressMap.getOrDefault(status.getImportId(), 0));
    }


    @Async
    @Override
    public void startImportAsync(String importId) throws InterruptedException, TissClientException, ValidationException {
        startImport(importId);
    }

    /**
     * Starts the Tiss import process asynchronously.
     *
     * @param importId the import ID
     * @throws InterruptedException if the import process is interrupted
     * @throws TissClientException if an error occurs while fetching data from Tiss
     */
    @Override
    public void startImport(String importId) throws InterruptedException, TissClientException, ValidationException {
        LOGGER.info("Starting import with ID: {}", importId);
        ImportStatus lastImport = importStatusRepository.findLastImportStatus();

        if (lastImport != null && lastImport.getStatus() == ImportStatus.Status.RUNNING) {
            throw new ValidationException("An other import with ID " + lastImport.getImportId() + " is already running. Please wait for it to complete.");
        }

        importStatusRepository.save(new ImportStatus(null, importId, ImportStatus.Status.RUNNING, new Date()));
        TransactionStatus status = initiateTransaction("startImportTransaction");

        try {
            this.startImportTransaction(importId);
            transactionManager.commit(status);
        } catch (InterruptedException e) {
            // Handle InterruptedException
            // The import was cancelled
            transactionManager.rollback(status);
            LOGGER.warn("Import with ID {} was interrupted", importId, e);
            throw e;
        } catch (Exception e) {
            // Handle other exceptions
            // Import Failed
            transactionManager.rollback(status);
            handleImportFailure(importId, e);
            throw e;
        } finally {
            progressMap.remove(importId);
        }
    }


    /**
     * Start the transactional part of the Tiss import process asynchronously.
     *
     * @param importId the import ID
     * @throws TissClientException if an error occurs while fetching data from Tiss
     * @throws InterruptedException if the import process is interrupted
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, rollbackFor = {Exception.class, InterruptedException.class})
    public void startImportTransaction(String importId) throws TissClientException, InterruptedException {

        //Load all orgUnits from Tiss organigram
        var orgUnits = this.tissClient.getOrgUnits();

        //Import subjects for each orgUnit/faculty
        for (int i = 0; i < orgUnits.size(); i++) {

            //Check for cancellation before each batch of subjects
            checkForCancellation(importId);

            //Get all subjects for one orgUnit
            List<Subject> tissSubjects = this.tissClient.getOrgUnitCourses(orgUnits.get(i));

            //Filter already existing subjects
            List<Subject> newSubjects = filterNewSubjects(tissSubjects);

            //Bulk insert
            if (!newSubjects.isEmpty()) {
                subjectRepository.saveAll(newSubjects);
            }

            //Set the progress in the progressMap
            updateProgress(importId, i, orgUnits.size());
        }

        completeImport(importId);
        LOGGER.info("Import with ID {} completed successfully", importId);
    }


    /**
     * Retrieves the status of a Tiss import process.
     *
     * @param importId the import ID
     * @return the import status DTO
     */
    @Override
    public ImportStatusDto getImportStatus(String importId) {
        ImportStatus status = this.importStatusRepository.findByImportId(importId);
        if (status == null) {
            throw new NotFoundException("No Import found with this id: " + importId);
        }
        return mapper.importStatusToDto(status, progressMap.getOrDefault(importId, 0));
    }

    /**
     * Cancels the Tiss import process.
     *
     * @param importId the import ID
     */
    @Override
    public void cancelImport(String importId) {
        ImportStatus status = importStatusRepository.findByImportId(importId);
        if (status != null && status.getStatus() == ImportStatus.Status.RUNNING) {
            status.setStatus(ImportStatus.Status.CANCELLED);
            importStatusRepository.save(status);
            cancellationMap.put(importId, true);
            progressMap.remove(importId);
            LOGGER.info("Import with ID {} has been cancelled", importId);
        }
    }

    /**
     * Checks if the import process has been cancelled.
     *
     * @param importId the import ID
     * @throws InterruptedException if the import process is cancelled
     */
    private void checkForCancellation(String importId) throws InterruptedException {
        if (Boolean.TRUE.equals(cancellationMap.get(importId))) {
            throw new InterruptedException("Import with ID " + importId + " was cancelled.");
        }
    }

    /**
     * Filters out the subjects that already exist in the repository.
     *
     * @param tissSubjects the list of subjects fetched from Tiss
     * @return a list of new subjects to be imported
     */
    private List<Subject> filterNewSubjects(List<Subject> tissSubjects) {
        List<Subject> newSubjects = new ArrayList<>();
        for (Subject subject : tissSubjects) {
            if (!subjectRepository.existsByTypeAndSemesterAndNumber(subject.getType(), subject.getSemester(), subject.getNumber())) {
                newSubjects.add(subject);
            }
        }
        return newSubjects;
    }

    /**
     * Updates the progress of the import process.
     *
     * @param importId the import ID
     * @param currentIndex the current index of the processed org unit
     * @param totalUnits the total number of org units
     */
    private void updateProgress(String importId, int currentIndex, int totalUnits) {
        int progress = (currentIndex + 1) * 100 / totalUnits;
        progressMap.put(importId, progress);
    }

    /**
     * Marks the import process as completed.
     *
     * @param importId the import ID
     */
    private void completeImport(String importId) {
        ImportStatus status = importStatusRepository.findByImportId(importId);
        status.setStatus(ImportStatus.Status.COMPLETED);
        status.setImportDate(new Date());
        importStatusRepository.save(status);
        progressMap.remove(importId);
    }

    /**
     * Initiates a transaction.
     *
     * @param transactionName the name of the transaction
     * @return the initiated transaction class
     */
    private TransactionStatus initiateTransaction(String transactionName) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName(transactionName);
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return transactionManager.getTransaction(def);
    }


    /**
     * Handles an import failure scenario.
     *
     * @param importId the import ID
     * @param e the exception that caused the failure
     */
    private void handleImportFailure(String importId, Exception e) {
        ImportStatus status = importStatusRepository.findByImportId(importId);
        if (status != null) {
            status.setStatus(ImportStatus.Status.FAILED);
            importStatusRepository.save(status);
        }
        LOGGER.error("Import with ID {} failed", importId, e);
    }
}
