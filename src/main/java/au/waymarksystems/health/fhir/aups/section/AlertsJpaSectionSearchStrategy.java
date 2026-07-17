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
package au.waymarksystems.health.fhir.aups.section;

import ca.uhn.fhir.jpa.ips.api.IpsSectionContext;
import ca.uhn.fhir.jpa.ips.jpa.JpaSectionSearchStrategy;
import jakarta.annotation.Nonnull;
import org.hl7.fhir.r4.model.Flag;

/**
 * Includes active clinical alert {@link Flag} resources in the AU PS Alerts section.
 */
public class AlertsJpaSectionSearchStrategy extends JpaSectionSearchStrategy<Flag> {

	@Override
	public boolean shouldInclude(@Nonnull IpsSectionContext<Flag> theIpsSectionContext, @Nonnull Flag theCandidate) {
		return theCandidate.getStatus() == Flag.FlagStatus.ACTIVE;
	}
}
