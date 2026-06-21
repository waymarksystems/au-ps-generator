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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.ips.api.IIpsGenerationStrategy;
import ca.uhn.fhir.jpa.ips.generator.IIpsGeneratorSvc;
import ca.uhn.fhir.jpa.ips.provider.IpsOperationProvider;
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
			assertThat(context).doesNotHaveBean(IpsOperationProvider.class);
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
			assertThat(context).hasSingleBean(IpsOperationProvider.class);
		});
	}

	@Configuration
	static class FhirContextConfig {
		@Bean
		FhirContext fhirContext() {
			return FhirContext.forR4Cached();
		}

		@Bean
		ca.uhn.fhir.jpa.api.dao.DaoRegistry daoRegistry() {
			// The AU PS strategy autowires a DaoRegistry; a stub is sufficient for wiring tests.
			return mock(ca.uhn.fhir.jpa.api.dao.DaoRegistry.class);
		}
	}
}
