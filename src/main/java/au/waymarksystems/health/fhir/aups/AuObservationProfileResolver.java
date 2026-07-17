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

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Observation;

import java.util.Optional;

/**
 * Resolves the appropriate AU PS Observation profile for a given Observation.
 *
 * <p>AU PS defines mutually exclusive Observation profiles. This resolver chooses among them
 * using Observation.code / category heuristics aligned with the IG:</p>
 * <ul>
 *     <li>LOINC {@code 72166-2} → {@link AuPsConstants#AU_PS_SMOKING_STATUS_PROFILE}</li>
 *     <li>category {@code laboratory} → {@link AuPsConstants#AU_PS_PATHOLOGY_RESULT_PROFILE}</li>
 * </ul>
 *
 * <p>Observations that cannot be confidently classified (for example vital signs or pregnancy
 * observations covered by IPS profiles only) are left unstamped.</p>
 */
public final class AuObservationProfileResolver {

	private AuObservationProfileResolver() {
		// utility
	}

	/**
	 * @return the AU PS Observation profile URL to stamp, or empty if none applies
	 */
	public static Optional<String> resolveProfile(Observation theObservation) {
		if (theObservation == null) {
			return Optional.empty();
		}

		if (hasLoincCode(theObservation, AuPsConstants.LOINC_SMOKING_STATUS)) {
			return Optional.of(AuPsConstants.AU_PS_SMOKING_STATUS_PROFILE);
		}

		if (hasCategory(theObservation, AuPsConstants.PATHOLOGY_OBSERVATION_CATEGORIES)) {
			return Optional.of(AuPsConstants.AU_PS_PATHOLOGY_RESULT_PROFILE);
		}

		return Optional.empty();
	}

	private static boolean hasLoincCode(Observation theObservation, String theCode) {
		if (!theObservation.hasCode()) {
			return false;
		}
		for (Coding coding : theObservation.getCode().getCoding()) {
			if (AuPsConstants.LOINC_SYSTEM.equals(coding.getSystem()) && theCode.equals(coding.getCode())) {
				return true;
			}
		}
		return false;
	}

	private static boolean hasCategory(Observation theObservation, java.util.Set<String> theCategories) {
		for (CodeableConcept category : theObservation.getCategory()) {
			for (Coding coding : category.getCoding()) {
				if (AuPsConstants.OBSERVATION_CATEGORY_SYSTEM.equals(coding.getSystem())
						&& theCategories.contains(coding.getCode())) {
					return true;
				}
			}
		}
		return false;
	}
}
