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

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Observation;
import org.junit.jupiter.api.Test;

class AuObservationProfileResolverTest {

	@Test
	void resolvesSmokingStatusByLoincCode() {
		Observation observation = new Observation();
		observation
				.getCode()
				.addCoding(new Coding(AuPsConstants.LOINC_SYSTEM, AuPsConstants.LOINC_SMOKING_STATUS, "Smoking status"));

		assertThat(AuObservationProfileResolver.resolveProfile(observation))
				.contains(AuPsConstants.AU_PS_SMOKING_STATUS_PROFILE);
	}

	@Test
	void resolvesPathologyByLaboratoryCategory() {
		Observation observation = new Observation();
		observation.addCategory(new CodeableConcept()
				.addCoding(new Coding(AuPsConstants.OBSERVATION_CATEGORY_SYSTEM, "laboratory", "Laboratory")));

		assertThat(AuObservationProfileResolver.resolveProfile(observation))
				.contains(AuPsConstants.AU_PS_PATHOLOGY_RESULT_PROFILE);
	}

	@Test
	void prefersSmokingStatusWhenBothHeuristicsMatch() {
		Observation observation = new Observation();
		observation
				.getCode()
				.addCoding(new Coding(AuPsConstants.LOINC_SYSTEM, AuPsConstants.LOINC_SMOKING_STATUS, "Smoking status"));
		observation.addCategory(new CodeableConcept()
				.addCoding(new Coding(AuPsConstants.OBSERVATION_CATEGORY_SYSTEM, "laboratory", "Laboratory")));

		assertThat(AuObservationProfileResolver.resolveProfile(observation))
				.contains(AuPsConstants.AU_PS_SMOKING_STATUS_PROFILE);
	}

	@Test
	void leavesVitalSignsUnstamped() {
		Observation observation = new Observation();
		observation.addCategory(new CodeableConcept()
				.addCoding(new Coding(AuPsConstants.OBSERVATION_CATEGORY_SYSTEM, "vital-signs", "Vital Signs")));
		observation.getCode().addCoding(new Coding(AuPsConstants.LOINC_SYSTEM, "8867-4", "Heart rate"));

		assertThat(AuObservationProfileResolver.resolveProfile(observation)).isEmpty();
	}

	@Test
	void returnsEmptyForNullOrEmptyObservation() {
		assertThat(AuObservationProfileResolver.resolveProfile(null)).isEmpty();
		assertThat(AuObservationProfileResolver.resolveProfile(new Observation())).isEmpty();
	}
}
