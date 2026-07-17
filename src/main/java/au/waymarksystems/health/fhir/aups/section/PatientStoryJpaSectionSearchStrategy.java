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
package au.waymarksystems.health.fhir.aups.section;

import ca.uhn.fhir.jpa.ips.api.IpsSectionContext;
import ca.uhn.fhir.jpa.ips.jpa.JpaSectionSearchStrategy;
import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import ca.uhn.fhir.rest.param.TokenOrListParam;
import ca.uhn.fhir.rest.param.TokenParam;
import jakarta.annotation.Nonnull;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.Enumerations;

import au.waymarksystems.health.fhir.aups.AuPsConstants;

/**
 * Includes patient-authored story {@link DocumentReference} resources in the AU PS Patient Story
 * section (LOINC {@code 81338-6}).
 */
public class PatientStoryJpaSectionSearchStrategy extends JpaSectionSearchStrategy<DocumentReference> {

	@Override
	public void massageResourceSearch(
			@Nonnull IpsSectionContext<DocumentReference> theIpsSectionContext,
			@Nonnull SearchParameterMap theSearchParameterMap) {
		theSearchParameterMap.add(
				DocumentReference.SP_TYPE,
				new TokenOrListParam()
						.addOr(new TokenParam(AuPsConstants.LOINC_SYSTEM, AuPsConstants.SECTION_CODE_PATIENT_STORY)));
	}

	@Override
	public boolean shouldInclude(
			@Nonnull IpsSectionContext<DocumentReference> theIpsSectionContext,
			@Nonnull DocumentReference theCandidate) {
		return theCandidate.getStatus() == Enumerations.DocumentReferenceStatus.CURRENT;
	}
}
