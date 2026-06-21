package au.gov.health.fhir.aups;

import ca.uhn.fhir.rest.api.server.RequestDetails;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.StringType;

/**
 * Service interface for generating AU Patient Summary documents.
 *
 * <p>Implementations of this interface are responsible for assembling a FHIR Document Bundle
 * that conforms to the AU Patient Summary FHIR Implementation Guide.
 */
public interface AuPatientSummaryGeneratorSvc {

    /**
     * Generates an AU Patient Summary document for the given patient.
     *
     * @param thePatientId the logical ID of the Patient resource
     * @param theProfile   optional profile URL to constrain the summary to
     * @param theRequestDetails the current request details
     * @return a Bundle of type {@code document} containing the AU Patient Summary
     */
    Bundle generateAuPatientSummary(
            IdType thePatientId,
            StringType theProfile,
            RequestDetails theRequestDetails);
}
