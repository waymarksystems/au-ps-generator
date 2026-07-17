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

import static org.assertj.core.api.Assertions.assertThat;

import au.waymarksystems.health.fhir.aups.section.AlertsJpaSectionSearchStrategy;
import au.waymarksystems.health.fhir.aups.section.PatientStoryJpaSectionSearchStrategy;
import ca.uhn.fhir.jpa.ips.api.IpsSectionContext;
import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Flag;
import org.junit.jupiter.api.Test;

class AuPsSectionSearchStrategyTest {

	@Test
	void alertsIncludesOnlyActiveFlags() {
		AlertsJpaSectionSearchStrategy strategy = new AlertsJpaSectionSearchStrategy();
		IpsSectionContext<Flag> context = org.mockito.Mockito.mock(IpsSectionContext.class);

		Flag active = new Flag();
		active.setStatus(Flag.FlagStatus.ACTIVE);
		Flag inactive = new Flag();
		inactive.setStatus(Flag.FlagStatus.INACTIVE);

		assertThat(strategy.shouldInclude(context, active)).isTrue();
		assertThat(strategy.shouldInclude(context, inactive)).isFalse();
	}

	@Test
	void patientStoryFiltersByLoincTypeAndCurrentStatus() {
		PatientStoryJpaSectionSearchStrategy strategy = new PatientStoryJpaSectionSearchStrategy();
		IpsSectionContext<DocumentReference> context = org.mockito.Mockito.mock(IpsSectionContext.class);

		SearchParameterMap map = new SearchParameterMap();
		strategy.massageResourceSearch(context, map);
		assertThat(map.get(DocumentReference.SP_TYPE)).isNotNull().isNotEmpty();
		assertThat(map.get(DocumentReference.SP_TYPE).toString()).contains(AuPsConstants.SECTION_CODE_PATIENT_STORY);

		DocumentReference current = new DocumentReference();
		current.setStatus(Enumerations.DocumentReferenceStatus.CURRENT);
		DocumentReference superseded = new DocumentReference();
		superseded.setStatus(Enumerations.DocumentReferenceStatus.SUPERSEDED);

		assertThat(strategy.shouldInclude(context, current)).isTrue();
		assertThat(strategy.shouldInclude(context, superseded)).isFalse();
	}
}
