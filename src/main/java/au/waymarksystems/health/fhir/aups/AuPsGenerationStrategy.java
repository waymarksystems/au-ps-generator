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

import au.waymarksystems.health.fhir.aups.section.AlertsJpaSectionSearchStrategy;
import au.waymarksystems.health.fhir.aups.section.PatientStoryJpaSectionSearchStrategy;
import ca.uhn.fhir.jpa.ips.api.IpsContext;
import ca.uhn.fhir.jpa.ips.api.Section;
import ca.uhn.fhir.jpa.ips.jpa.DefaultJpaIpsGenerationStrategy;
import ca.uhn.fhir.jpa.ips.jpa.JpaSectionSearchStrategyCollection;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.Flag;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Resource;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * AU Patient Summary generation strategy.
 *
 * <p>This strategy extends HAPI FHIR's {@link DefaultJpaIpsGenerationStrategy}, which pulls all
 * applicable patient data from the HAPI FHIR JPA repository (one search per IPS section), and
 * then remaps the generated document so that it conforms to the
 * <a href="https://hl7.org.au/fhir/ps/">AU Patient Summary (AU PS) FHIR Implementation Guide</a>.</p>
 *
 * <p>The remapping performed here is:</p>
 * <ul>
 *     <li>The document {@code Bundle} claims conformance to the AU PS Bundle profile.</li>
 *     <li>The {@code Composition} claims conformance to the AU PS Composition profile and is
 *     tagged with the {@code en-AU} language.</li>
 *     <li>Every contained resource that has an unambiguous AU PS profile is stamped with that
 *     profile (see {@link AuPsConstants#RESOURCE_TYPE_TO_PROFILE}).</li>
 *     <li>{@code Observation} resources are stamped when they can be classified as smoking
 *     status or pathology results (see {@link AuObservationProfileResolver}).</li>
 * </ul>
 *
 * <p>In addition to the standard IPS sections, this strategy registers the optional AU PS
 * Alerts and Patient Story sections when matching resources are present.</p>
 */
public class AuPsGenerationStrategy extends DefaultJpaIpsGenerationStrategy {

	/**
	 * Narrative property file shipped with this plugin. It defines the AU PS section narrative
	 * templates and may be overridden by implementers (see the project README).
	 */
	public static final String AU_PS_NARRATIVES_PROPERTIES = "classpath:au/ps/narrative/aups-narratives.properties";

	/** Composition section profile key used by narrative matching for Alerts. */
	public static final String SECTION_PROFILE_ALERTS =
			"https://hl7.org/fhir/uv/ips/StructureDefinition-Composition-uv-ips-definitions.html#Composition.section:sectionAlerts";

	/** Composition section profile key used by narrative matching for Patient Story. */
	public static final String SECTION_PROFILE_PATIENT_STORY =
			"http://hl7.org.au/fhir/ps/StructureDefinition/au-ps-composition#Composition.section:sectionPatientStory";

	@Override
	public String getBundleProfile() {
		return AuPsConstants.AU_PS_BUNDLE_PROFILE;
	}

	@Override
	public List<String> getNarrativePropertyFiles() {
		// Use the AU PS narrative templates, falling back to the bundled IPS templates for
		// anything the AU PS file does not explicitly override.
		return List.of(AU_PS_NARRATIVES_PROPERTIES, DEFAULT_IPS_NARRATIVES_PROPERTIES);
	}

	@Override
	public String createTitle(IpsContext theContext) {
		return "Australian Patient Summary as of "
				+ DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDate.now());
	}

	@Override
	public org.hl7.fhir.instance.model.api.IBaseResource createAuthor() {
		Organization organization = new Organization();
		organization
				.setName("AU Patient Summary Generator")
				.addAddress(new Address().setCountry("AU"))
				.setId(IdType.newRandomUuid());
		return organization;
	}

	/**
	 * Register the standard IPS sections plus the optional AU PS Alerts and Patient Story
	 * sections. Empty optional sections are omitted by the IPS generator when no matching
	 * resources are found.
	 */
	@Override
	protected void addSections() {
		super.addSections();
		addJpaSectionAlerts();
		addJpaSectionPatientStory();
	}

	protected void addJpaSectionAlerts() {
		Section section = Section.newBuilder()
				.withTitle("Alerts")
				.withSectionSystem(AuPsConstants.LOINC_SYSTEM)
				.withSectionCode(AuPsConstants.SECTION_CODE_ALERTS)
				.withSectionDisplay("Alert")
				.withResourceType(Flag.class)
				.withProfile(SECTION_PROFILE_ALERTS)
				.build();

		JpaSectionSearchStrategyCollection searchStrategyCollection = JpaSectionSearchStrategyCollection.newBuilder()
				.addStrategy(Flag.class, new AlertsJpaSectionSearchStrategy())
				.build();

		addJpaSection(section, searchStrategyCollection);
	}

	protected void addJpaSectionPatientStory() {
		Section section = Section.newBuilder()
				.withTitle("Patient Story")
				.withSectionSystem(AuPsConstants.LOINC_SYSTEM)
				.withSectionCode(AuPsConstants.SECTION_CODE_PATIENT_STORY)
				.withSectionDisplay("Patient Narrative")
				.withResourceType(DocumentReference.class)
				.withProfile(SECTION_PROFILE_PATIENT_STORY)
				.build();

		JpaSectionSearchStrategyCollection searchStrategyCollection = JpaSectionSearchStrategyCollection.newBuilder()
				.addStrategy(DocumentReference.class, new PatientStoryJpaSectionSearchStrategy())
				.build();

		addJpaSection(section, searchStrategyCollection);
	}

	/**
	 * Remap the generated IPS document into AU PS conformant resources. This is invoked once per
	 * generated document, after all sections have been assembled.
	 */
	@Override
	public void postManipulateIpsBundle(IBaseBundle theBundle) {
		if (!(theBundle instanceof Bundle bundle)) {
			return;
		}

		for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
			Resource resource = entry.getResource();
			if (resource == null) {
				continue;
			}

			if (resource instanceof Composition composition) {
				addProfileIfAbsent(composition, AuPsConstants.AU_PS_COMPOSITION_PROFILE);
				if (!composition.hasLanguage()) {
					composition.setLanguage(AuPsConstants.AU_LANGUAGE);
				}
				continue;
			}

			if (resource instanceof Observation observation) {
				AuObservationProfileResolver.resolveProfile(observation)
						.ifPresent(profile -> addProfileIfAbsent(observation, profile));
				continue;
			}

			String profile = AuPsConstants.RESOURCE_TYPE_TO_PROFILE.get(resource.fhirType());
			if (profile != null) {
				addProfileIfAbsent(resource, profile);
			}
		}
	}

	private static void addProfileIfAbsent(Resource theResource, String theProfile) {
		boolean alreadyPresent = theResource.getMeta().getProfile().stream()
				.anyMatch(p -> theProfile.equals(p.getValueAsString()));
		if (!alreadyPresent) {
			theResource.getMeta().addProfile(theProfile);
		}
	}
}
