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

import ca.uhn.fhir.jpa.ips.generator.IIpsGeneratorSvc;
import ca.uhn.fhir.jpa.ips.provider.IpsOperationProvider;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * FHIR Operation Provider that exposes the AU Patient Summary {@code Patient/$summary} operation.
 *
 * <p>This provider extends the base IPS operation provider so that HAPI FHIR JPA starter servers
 * that inject {@link IpsOperationProvider} pick it up automatically. Generation uses the wired
 * {@link IIpsGeneratorSvc}, which in turn uses {@link AuPsGenerationStrategy}.</p>
 *
 * <p>To enable, set the following in your {@code application.yaml}:</p>
 * <pre>
 *   hapi.fhir.aups_enabled: true
 * </pre>
 */
public class AuPatientSummaryGeneratorProvider extends IpsOperationProvider {

	@Autowired
	public AuPatientSummaryGeneratorProvider(IIpsGeneratorSvc theIpsGeneratorSvc) {
		super(theIpsGeneratorSvc);
	}
}
