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
import static org.mockito.Mockito.mock;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.api.dao.DaoRegistry;
import ca.uhn.fhir.jpa.ips.api.IpsContext;
import ca.uhn.fhir.jpa.ips.api.Section;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.r4.model.AllergyIntolerance;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class AuPsGenerationStrategyTest {

	private static final FhirContext ourFhirContext = FhirContext.forR4Cached();

	private AuPsGenerationStrategy myStrategy;

	@BeforeEach
	void setUp() {
		myStrategy = new AuPsGenerationStrategy();
	}

	@Test
	void bundleProfileIsAuPsBundle() {
		assertThat(myStrategy.getBundleProfile()).isEqualTo(AuPsConstants.AU_PS_BUNDLE_PROFILE);
	}

	@Test
	void narrativePropertyFilesPreferAuPsThenFallBackToIps() {
		assertThat(myStrategy.getNarrativePropertyFiles())
				.containsExactly(
						AuPsGenerationStrategy.AU_PS_NARRATIVES_PROPERTIES,
						AuPsGenerationStrategy.DEFAULT_IPS_NARRATIVES_PROPERTIES);
	}

	@Test
	void titleIsAustralianPatientSummary() {
		IpsContext context = new IpsContext(new Patient(), new IdType("Patient/123"));
		assertThat(myStrategy.createTitle(context)).startsWith("Australian Patient Summary as of ");
	}

	@Test
	void authorIsAnAustralianOrganization() {
		assertThat(myStrategy.createAuthor()).isInstanceOf(Organization.class);
		Organization author = (Organization) myStrategy.createAuthor();
		assertThat(author.getName()).isNotBlank();
		assertThat(author.getAddressFirstRep().getCountry()).isEqualTo("AU");
		assertThat(author.getIdElement().getValue()).startsWith("urn:uuid:");
	}

	@Test
	void postManipulateStampsCompositionProfileAndLanguage() {
		Composition composition = new Composition();
		Bundle bundle = bundleOf(composition);

		myStrategy.postManipulateIpsBundle(bundle);

		assertThat(profilesOf(composition)).containsExactly(AuPsConstants.AU_PS_COMPOSITION_PROFILE);
		assertThat(composition.getLanguage()).isEqualTo(AuPsConstants.AU_LANGUAGE);
	}

	@Test
	void postManipulatePreservesExistingCompositionLanguage() {
		Composition composition = new Composition();
		composition.setLanguage("en");
		Bundle bundle = bundleOf(composition);

		myStrategy.postManipulateIpsBundle(bundle);

		assertThat(composition.getLanguage()).isEqualTo("en");
	}

	@Test
	void postManipulateStampsKnownResourceProfiles() {
		Patient patient = new Patient();
		AllergyIntolerance allergy = new AllergyIntolerance();
		Bundle bundle = bundleOf(patient, allergy);

		myStrategy.postManipulateIpsBundle(bundle);

		assertThat(profilesOf(patient)).containsExactly(AuPsConstants.AU_PS_PATIENT_PROFILE);
		assertThat(profilesOf(allergy))
				.containsExactly("http://hl7.org.au/fhir/ps/StructureDefinition/au-ps-allergyintolerance");
	}

	@Test
	void postManipulateStampsSmokingStatusObservation() {
		Observation observation = new Observation();
		observation
				.getCode()
				.addCoding(new Coding(AuPsConstants.LOINC_SYSTEM, AuPsConstants.LOINC_SMOKING_STATUS, "Smoking status"));
		Bundle bundle = bundleOf(observation);

		myStrategy.postManipulateIpsBundle(bundle);

		assertThat(profilesOf(observation)).containsExactly(AuPsConstants.AU_PS_SMOKING_STATUS_PROFILE);
	}

	@Test
	void postManipulateStampsPathologyObservation() {
		Observation observation = new Observation();
		observation.addCategory(new CodeableConcept()
				.addCoding(new Coding(AuPsConstants.OBSERVATION_CATEGORY_SYSTEM, "laboratory", "Laboratory")));
		Bundle bundle = bundleOf(observation);

		myStrategy.postManipulateIpsBundle(bundle);

		assertThat(profilesOf(observation)).containsExactly(AuPsConstants.AU_PS_PATHOLOGY_RESULT_PROFILE);
	}

	@Test
	void postManipulateLeavesUnclassifiedObservationsUntouched() {
		Observation observation = new Observation();
		observation.addCategory(new CodeableConcept()
				.addCoding(new Coding(AuPsConstants.OBSERVATION_CATEGORY_SYSTEM, "vital-signs", "Vital Signs")));
		Bundle bundle = bundleOf(observation);

		myStrategy.postManipulateIpsBundle(bundle);

		assertThat(observation.getMeta().getProfile()).isEmpty();
	}

	@Test
	void postManipulateIsIdempotentAndDoesNotDuplicateProfiles() {
		Patient patient = new Patient();
		Bundle bundle = bundleOf(patient);

		myStrategy.postManipulateIpsBundle(bundle);
		myStrategy.postManipulateIpsBundle(bundle);

		assertThat(profilesOf(patient)).containsExactly(AuPsConstants.AU_PS_PATIENT_PROFILE);
	}

	@Test
	void postManipulatePreservesPreExistingProfiles() {
		Patient patient = new Patient();
		patient.getMeta().addProfile("http://hl7.org.au/fhir/core/StructureDefinition/au-core-patient");
		Bundle bundle = bundleOf(patient);

		myStrategy.postManipulateIpsBundle(bundle);

		assertThat(profilesOf(patient))
				.containsExactly(
						"http://hl7.org.au/fhir/core/StructureDefinition/au-core-patient",
						AuPsConstants.AU_PS_PATIENT_PROFILE);
	}

	@Test
	void postManipulateIgnoresEntriesWithoutResources() {
		Bundle bundle = new Bundle();
		bundle.addEntry();

		myStrategy.postManipulateIpsBundle(bundle);

		assertThat(bundle.getEntry()).hasSize(1);
	}

	@Test
	void postManipulateIgnoresNonR4Bundles() {
		IBaseBundle notAnR4Bundle = mock(IBaseBundle.class);
		myStrategy.postManipulateIpsBundle(notAnR4Bundle);
	}

	@Test
	void initializeRegistersIpsSectionsPlusAuPsExtensions() {
		DaoRegistry daoRegistry = mock(DaoRegistry.class);
		myStrategy.setDaoRegistry(daoRegistry);
		myStrategy.setFhirContext(ourFhirContext);

		myStrategy.initialize();

		List<Section> sections = myStrategy.getSections();
		assertThat(sections).hasSize(16);
		assertThat(sections.stream().map(Section::getSectionCode))
				.contains(
						AuPsConstants.SECTION_CODE_ALERTS,
						AuPsConstants.SECTION_CODE_PATIENT_STORY,
						"48765-2",
						"11450-4",
						"10160-0");
		assertThat(sections.stream().map(Section::getProfile)).doesNotContainNull();
		assertThat(sections.stream().map(Section::getTitle))
				.contains("Alerts", "Patient Story", "Allergies and Intolerances", "Problem List");
	}

	private static Bundle bundleOf(org.hl7.fhir.r4.model.Resource... theResources) {
		Bundle bundle = new Bundle();
		for (org.hl7.fhir.r4.model.Resource resource : theResources) {
			bundle.addEntry().setResource(resource);
		}
		return bundle;
	}

	private static List<String> profilesOf(org.hl7.fhir.r4.model.Resource theResource) {
		return theResource.getMeta().getProfile().stream()
				.map(org.hl7.fhir.r4.model.PrimitiveType::getValueAsString)
				.toList();
	}
}
