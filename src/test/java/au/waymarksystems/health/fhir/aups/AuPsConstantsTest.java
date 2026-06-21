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

import org.hl7.fhir.r4.model.AllergyIntolerance;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.MedicationStatement;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Procedure;
import org.junit.jupiter.api.Test;

class AuPsConstantsTest {

	@Test
	void bundleAndCompositionProfilesUseAuPsCanonicalBase() {
		assertThat(AuPsConstants.AU_PS_BUNDLE_PROFILE)
				.isEqualTo("http://hl7.org.au/fhir/ps/StructureDefinition/au-ps-bundle");
		assertThat(AuPsConstants.AU_PS_COMPOSITION_PROFILE)
				.isEqualTo("http://hl7.org.au/fhir/ps/StructureDefinition/au-ps-composition");
		assertThat(AuPsConstants.AU_PS_PATIENT_PROFILE)
				.isEqualTo("http://hl7.org.au/fhir/ps/StructureDefinition/au-ps-patient");
	}

	@Test
	void resourceTypeMapContainsUnambiguousProfiles() {
		assertThat(AuPsConstants.RESOURCE_TYPE_TO_PROFILE)
				.containsEntry("Patient", "http://hl7.org.au/fhir/ps/StructureDefinition/au-ps-patient")
				.containsEntry(
						"AllergyIntolerance",
						"http://hl7.org.au/fhir/ps/StructureDefinition/au-ps-allergyintolerance")
				.containsEntry("Condition", "http://hl7.org.au/fhir/ps/StructureDefinition/au-ps-condition")
				.containsEntry("Immunization", "http://hl7.org.au/fhir/ps/StructureDefinition/au-ps-immunization")
				.containsEntry(
						"MedicationStatement",
						"http://hl7.org.au/fhir/ps/StructureDefinition/au-ps-medicationstatement")
				.containsEntry("Procedure", "http://hl7.org.au/fhir/ps/StructureDefinition/au-ps-procedure")
				.containsEntry("Practitioner", "http://hl7.org.au/fhir/ps/StructureDefinition/au-ps-practitioner");
	}

	@Test
	void resourceTypeMapKeysMatchFhirTypeNames() {
		// The keys are looked up via Resource.fhirType(), so they must match exactly.
		assertThat(AuPsConstants.RESOURCE_TYPE_TO_PROFILE).containsKeys(
				new Patient().fhirType(),
				new AllergyIntolerance().fhirType(),
				new Condition().fhirType(),
				new Immunization().fhirType(),
				new MedicationStatement().fhirType(),
				new Procedure().fhirType(),
				new Practitioner().fhirType());
	}

	@Test
	void ambiguousResourceTypesAreNotMapped() {
		// Observation has several mutually-exclusive AU PS profiles, so it must not be auto-mapped.
		assertThat(AuPsConstants.RESOURCE_TYPE_TO_PROFILE).doesNotContainKey("Observation");
	}

	@Test
	void everyMappedProfileUsesTheAuPsBase() {
		assertThat(AuPsConstants.RESOURCE_TYPE_TO_PROFILE.values())
				.allMatch(profile -> profile.startsWith(AuPsConstants.AU_PS_PROFILE_BASE));
	}

	@Test
	void organizationMapsToAuPsOrganization() {
		assertThat(AuPsConstants.RESOURCE_TYPE_TO_PROFILE.get(new Organization().fhirType()))
				.isEqualTo("http://hl7.org.au/fhir/ps/StructureDefinition/au-ps-organization");
	}
}
