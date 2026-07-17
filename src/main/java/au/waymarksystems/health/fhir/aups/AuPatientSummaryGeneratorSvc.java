/*
 * Copyright 2026 Waymark Systems Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package au.waymarksystems.health.fhir.aups;

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
