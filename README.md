# AU Patient Summary (AU PS) Generator

In FHIR, a Patient Summary (IPS et al) is expressed as a FHIR Document. The HAPI FHIR JPA server supports the automated generation of Patient Summary documents via the $summary operation.

This project is a plugin for the HAPI FHIR JPA server that implements the [AU Patient Summary FHIR IG](https://github.com/hl7au/au-fhir-ps) and generates an AU Patient Summary document.

## Getting Started

### Via Maven

The generator is published to [GitHub Packages](https://github.com/waymarksystems/au-ps-generator/packages) and can be added to your HAPI FHIR JPA server as a dependency.

**1. Authenticate with GitHub Packages**

GitHub Packages requires authentication. Add your credentials to `~/.m2/settings.xml`:

```xml
<settings>
  <servers>
    <server>
      <id>github-waymarksystems</id>
      <username>YOUR_GITHUB_USERNAME</username>
      <!-- Use a GitHub Personal Access Token (PAT) with read:packages scope -->
      <password>YOUR_GITHUB_TOKEN</password>
    </server>
  </servers>
</settings>
```

**2. Add the repository to your `pom.xml`**

```xml
<repositories>
  <repository>
    <id>github-waymarksystems</id>
    <name>GitHub Packages – Waymark Systems</name>
    <url>https://maven.pkg.github.com/waymarksystems/au-ps-generator</url>
  </repository>
</repositories>
```

**3. Add the dependency**

```xml
<dependency>
  <groupId>au.waymarksystems.health.fhir</groupId>
  <artifactId>au-ps-generator</artifactId>
  <version>1.0.0</version>
</dependency>
```

Replace `1.0.0` with the [latest released version](https://github.com/waymarksystems/au-ps-generator/packages).

### Via a JAR

You can also download the JAR directly from the [GitHub Packages page](https://github.com/waymarksystems/au-ps-generator/packages) and add it to your project classpath.


## Configuration

Once installed, register the `AuPatientSummaryGeneratorProvider` bean in your HAPI FHIR server Spring configuration:

```java
@Bean
public AuPatientSummaryGeneratorProvider auPatientSummaryGeneratorProvider() {
    return new AuPatientSummaryGeneratorProvider();
}
```

Then, in your `application.yaml` (or `application.properties`), enable the generator:

```yaml
hapi:
  fhir:
    aups_enabled: true
```

Or with properties syntax:

```properties
hapi.fhir.aups_enabled = true
```


## Narrative

This generator includes a basic narrative generator for each of the section types within the Patient Summary document. This narrative can be customised to suit your needs without breaking the conformance of the patient summary.

Example:

```properties
aups-allergyintolerance.resourceType=Bundle
aups-allergyintolerance.profile=https://hl7.org/fhir/uv/ips/StructureDefinition-Composition-uv-ips-definitions.html#Composition.section:sectionAllergies
aups-allergyintolerance.narrative=classpath:ca/uhn/fhir/jpa/ips/narrative/allergyintolerance.html
```


## Publishing

This project is automatically published to GitHub Packages on every push to `main` (as a SNAPSHOT) and on every `v*` tag (as a release).

To publish a release:

```bash
git tag v1.0.0
git push origin v1.0.0
```

The GitHub Actions workflow (`.github/workflows/publish.yml`) handles building and deploying to GitHub Packages using the `GITHUB_TOKEN` secret, which is automatically provided by GitHub Actions — no additional secrets configuration is required.

