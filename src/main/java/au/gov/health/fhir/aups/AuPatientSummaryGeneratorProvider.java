package au.gov.health.fhir.aups;

import ca.uhn.fhir.jpa.ips.provider.IpsOperationProvider;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * FHIR Operation Provider that implements the AU Patient Summary $summary operation.
 *
 * <p>This provider extends the base IPS operation to generate an AU Patient Summary document
 * that conforms to the AU Patient Summary FHIR Implementation Guide.
 *
 * <p>To enable, set the following in your {@code application.properties}:
 * <pre>
 *   hapi.fhir.aups_enabled = true
 * </pre>
 */
public class AuPatientSummaryGeneratorProvider extends IpsOperationProvider {

    @Autowired
    private AuPatientSummaryGeneratorSvc myAuPatientSummaryGeneratorSvc;

    /**
     * Implements the {@code $summary} operation on a Patient resource,
     * returning an AU Patient Summary document Bundle.
     *
     * @param thePatientId the logical ID of the Patient resource
     * @param theRequestDetails the current request details
     * @return a Bundle containing the AU Patient Summary document
     */
    @Operation(name = "$summary", idempotent = true, typeName = "Patient")
    public Bundle patientSummary(
            @IdParam IdType thePatientId,
            @OperationParam(name = "profile", min = 0) StringType theProfile,
            RequestDetails theRequestDetails) {
        return myAuPatientSummaryGeneratorSvc.generateAuPatientSummary(
                thePatientId, theProfile, theRequestDetails);
    }
}
