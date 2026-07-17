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
import ca.uhn.fhir.jpa.ips.api.IIpsGenerationStrategy;
import ca.uhn.fhir.jpa.ips.generator.IIpsGeneratorSvc;
import ca.uhn.fhir.jpa.ips.provider.IpsOperationProvider;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.StringType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

class AuPsConfigTest {

	private final ApplicationContextRunner myRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(AuPsConfig.class))
			.withUserConfiguration(FhirContextConfig.class);

	@Test
	void beansAreNotRegisteredWhenPropertyIsAbsent() {
		myRunner.run(context -> {
			assertThat(context).doesNotHaveBean(IIpsGenerationStrategy.class);
			assertThat(context).doesNotHaveBean(IIpsGeneratorSvc.class);
			assertThat(context).doesNotHaveBean(AuPatientSummaryGeneratorSvc.class);
			assertThat(context).doesNotHaveBean(AuPatientSummaryGeneratorProvider.class);
		});
	}

	@Test
	void beansAreNotRegisteredWhenPropertyIsFalse() {
		myRunner.withPropertyValues("hapi.fhir.aups_enabled=false").run(context -> {
			assertThat(context).doesNotHaveBean(IIpsGenerationStrategy.class);
		});
	}

	@Test
	void beansAreRegisteredWhenPropertyIsTrue() {
		myRunner.withPropertyValues("hapi.fhir.aups_enabled=true").run(context -> {
			assertThat(context).hasSingleBean(IIpsGenerationStrategy.class);
			assertThat(context.getBean(IIpsGenerationStrategy.class)).isInstanceOf(AuPsGenerationStrategy.class);
			assertThat(context).hasSingleBean(IIpsGeneratorSvc.class);
			assertThat(context).hasSingleBean(AuPatientSummaryGeneratorSvc.class);
			assertThat(context).hasSingleBean(AuPatientSummaryGeneratorProvider.class);
			assertThat(context.getBean(AuPatientSummaryGeneratorProvider.class))
					.isInstanceOf(IpsOperationProvider.class);
		});
	}

	@Test
	void serviceDelegatesToIpsGenerator() {
		IIpsGeneratorSvc ipsGeneratorSvc = mock(IIpsGeneratorSvc.class);
		Bundle expected = new Bundle();
		org.mockito.Mockito.when(ipsGeneratorSvc.generateIps(
						org.mockito.ArgumentMatchers.any(),
						org.mockito.ArgumentMatchers.any(IdType.class),
						org.mockito.ArgumentMatchers.any()))
				.thenReturn(expected);

		AuPatientSummaryGeneratorSvc svc = (thePatientId, theProfile, theRequestDetails) ->
				(Bundle) ipsGeneratorSvc.generateIps(
						theRequestDetails,
						thePatientId,
						theProfile != null ? theProfile.getValueAsString() : null);

		Bundle actual = svc.generateAuPatientSummary(
				new IdType("Patient/1"), new StringType(AuPsConstants.AU_PS_BUNDLE_PROFILE), mock(RequestDetails.class));

		assertThat(actual).isSameAs(expected);
	}

	@Configuration
	static class FhirContextConfig {
		@Bean
		FhirContext fhirContext() {
			return FhirContext.forR4Cached();
		}

		@Bean
		DaoRegistry daoRegistry() {
			return mock(DaoRegistry.class);
		}
	}
}
