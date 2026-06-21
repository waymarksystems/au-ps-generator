/*
 * Copyright 2026 Waymark Systems
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
package com.waymarksystems.fhir.aups;

import org.hl7.fhir.r4.model.AllergyIntolerance;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.Medication;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.MedicationStatement;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.RelatedPerson;

import java.util.Map;

/**
 * Canonical URLs and other constants defined by the
 * <a href="https://hl7.org.au/fhir/ps/">AU Patient Summary (AU PS) FHIR Implementation Guide</a>.
 *
 * <p>These are the profiles that resources in a generated AU PS document are claimed to conform to.</p>
 */
public final class AuPsConstants {

	/** Base canonical URL for all AU PS StructureDefinitions. */
	public static final String AU_PS_PROFILE_BASE = "http://hl7.org.au/fhir/ps/StructureDefinition/";

	/** Profile for the AU PS document Bundle. */
	public static final String AU_PS_BUNDLE_PROFILE = AU_PS_PROFILE_BASE + "au-ps-bundle";

	/** Profile for the AU PS Composition. */
	public static final String AU_PS_COMPOSITION_PROFILE = AU_PS_PROFILE_BASE + "au-ps-composition";

	/** Profile for the AU PS Patient (the subject of the summary). */
	public static final String AU_PS_PATIENT_PROFILE = AU_PS_PROFILE_BASE + "au-ps-patient";

	/** Language code applied to generated AU PS documents (Australian English). */
	public static final String AU_LANGUAGE = "en-AU";

	/**
	 * Mapping of FHIR R4 resource type name to the AU PS profile that the generator
	 * stamps onto each remapped resource. Only resource types with an unambiguous
	 * one-to-one AU PS profile are included. Resource types such as {@code Observation}
	 * are intentionally omitted because the IG defines several mutually exclusive
	 * profiles (for example smoking status and pathology result) that cannot be
	 * disambiguated by resource type alone.
	 */
	public static final Map<String, String> RESOURCE_TYPE_TO_PROFILE = Map.ofEntries(
			Map.entry(Patient.class.getSimpleName(), AU_PS_PATIENT_PROFILE),
			Map.entry(AllergyIntolerance.class.getSimpleName(), AU_PS_PROFILE_BASE + "au-ps-allergyintolerance"),
			Map.entry(Condition.class.getSimpleName(), AU_PS_PROFILE_BASE + "au-ps-condition"),
			Map.entry(Encounter.class.getSimpleName(), AU_PS_PROFILE_BASE + "au-ps-encounter"),
			Map.entry(Immunization.class.getSimpleName(), AU_PS_PROFILE_BASE + "au-ps-immunization"),
			Map.entry(Medication.class.getSimpleName(), AU_PS_PROFILE_BASE + "au-ps-medication"),
			Map.entry(MedicationRequest.class.getSimpleName(), AU_PS_PROFILE_BASE + "au-ps-medicationrequest"),
			Map.entry(MedicationStatement.class.getSimpleName(), AU_PS_PROFILE_BASE + "au-ps-medicationstatement"),
			Map.entry(Organization.class.getSimpleName(), AU_PS_PROFILE_BASE + "au-ps-organization"),
			Map.entry(Practitioner.class.getSimpleName(), AU_PS_PROFILE_BASE + "au-ps-practitioner"),
			Map.entry(PractitionerRole.class.getSimpleName(), AU_PS_PROFILE_BASE + "au-ps-practitionerrole"),
			Map.entry(Procedure.class.getSimpleName(), AU_PS_PROFILE_BASE + "au-ps-procedure"),
			Map.entry(RelatedPerson.class.getSimpleName(), AU_PS_PROFILE_BASE + "au-ps-relatedperson"));

	private AuPsConstants() {
		// constants holder
	}
}
