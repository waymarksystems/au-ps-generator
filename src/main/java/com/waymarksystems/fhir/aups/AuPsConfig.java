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

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.ips.api.IIpsGenerationStrategy;
import ca.uhn.fhir.jpa.ips.generator.IIpsGeneratorSvc;
import ca.uhn.fhir.jpa.ips.generator.IpsGeneratorSvcImpl;
import ca.uhn.fhir.jpa.ips.provider.IpsOperationProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring auto-configuration that wires the AU Patient Summary generator into a HAPI FHIR JPA
 * server.
 *
 * <p>This configuration is only active when the following property is set:</p>
 * <pre>
 * hapi.fhir.aups_enabled = true
 * </pre>
 *
 * <p>It registers the {@link AuPsGenerationStrategy} along with the IPS generator service and the
 * {@code $summary} operation provider, mirroring the standard HAPI FHIR IPS wiring.</p>
 */
@Configuration
@ConditionalOnProperty(name = "hapi.fhir.aups_enabled", havingValue = "true")
public class AuPsConfig {

	/**
	 * The AU PS generation strategy. The host server is expected to autowire the
	 * {@code DaoRegistry} and {@code FhirContext} into this bean before {@code initialize()} is
	 * invoked by the IPS generator service.
	 */
	@Bean
	@ConditionalOnMissingBean(IIpsGenerationStrategy.class)
	public IIpsGenerationStrategy auPsGenerationStrategy() {
		return new AuPsGenerationStrategy();
	}

	@Bean
	@ConditionalOnMissingBean(IIpsGeneratorSvc.class)
	public IIpsGeneratorSvc auPsGeneratorSvc(
			FhirContext theFhirContext, IIpsGenerationStrategy theGenerationStrategy) {
		return new IpsGeneratorSvcImpl(theFhirContext, theGenerationStrategy);
	}

	@Bean
	@ConditionalOnMissingBean(IpsOperationProvider.class)
	public IpsOperationProvider auPsOperationProvider(IIpsGeneratorSvc theIpsGeneratorSvc) {
		return new IpsOperationProvider(theIpsGeneratorSvc);
	}
}
