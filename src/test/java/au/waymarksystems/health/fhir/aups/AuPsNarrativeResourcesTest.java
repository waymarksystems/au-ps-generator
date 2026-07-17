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

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Properties;

class AuPsNarrativeResourcesTest {

	@Test
	void narrativePropertiesPointAtClasspathAuTemplates() throws Exception {
		Properties properties = new Properties();
		try (InputStream in = getClass().getResourceAsStream("/au/ps/narrative/aups-narratives.properties")) {
			assertThat(in).as("aups-narratives.properties must be on the classpath").isNotNull();
			properties.load(in);
		}

		assertThat(properties.getProperty("aups-global.narrative"))
				.isEqualTo("classpath:au/ps/narrative/composition.html");
		assertThat(properties.getProperty("aups-allergyintolerance.narrative"))
				.isEqualTo("classpath:au/ps/narrative/allergyintolerance.html");
		assertThat(properties.getProperty("aups-alerts.narrative"))
				.isEqualTo("classpath:au/ps/narrative/alerts.html");
		assertThat(properties.getProperty("aups-patientstory.narrative"))
				.isEqualTo("classpath:au/ps/narrative/patientstory.html");
		assertThat(properties.getProperty("aups-utility-fragments.fragmentName")).isEqualTo("IpsUtilityFragments");
	}

	@Test
	void requiredNarrativeTemplatesArePresent() {
		assertThat(getClass().getResource("/au/ps/narrative/composition.html")).isNotNull();
		assertThat(getClass().getResource("/au/ps/narrative/allergyintolerance.html")).isNotNull();
		assertThat(getClass().getResource("/au/ps/narrative/alerts.html")).isNotNull();
		assertThat(getClass().getResource("/au/ps/narrative/patientstory.html")).isNotNull();
		assertThat(getClass().getResource("/au/ps/narrative/utility-fragments.html")).isNotNull();
		assertThat(getClass().getResource("/au/ps/narrative/immunizations.html")).isNotNull();
	}

	@Test
	void compositionNarrativeUsesAustralianTitle() throws Exception {
		try (InputStream in = getClass().getResourceAsStream("/au/ps/narrative/composition.html")) {
			assertThat(in).isNotNull();
			String html = new String(in.readAllBytes());
			assertThat(html).contains("Australian Patient Summary");
			assertThat(html).doesNotContain("International Patient Summary Document");
		}
	}
}
