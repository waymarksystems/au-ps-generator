# AU Patient Summary (AU PS) Generator

A HAPI FHIR JPA server plugin that implements the
[AU Patient Summary FHIR IG](https://hl7.org.au/fhir/ps/) and generates an Australian Patient
Summary document via the FHIR `Patient/$summary` operation.

It builds on the HAPI FHIR International Patient Summary (IPS) generator. When `$summary` is
invoked, the generator searches the JPA repository (one search per summary section) and remaps the
resulting document so that it conforms to the AU PS IG:

* the document `Bundle` claims conformance to the
  [AU PS Bundle](http://hl7.org.au/fhir/ps/StructureDefinition/au-ps-bundle) profile
* the `Composition` claims conformance to the
  [AU PS Composition](http://hl7.org.au/fhir/ps/StructureDefinition/au-ps-composition) profile and
  is tagged with the `en-AU` language
* unambiguous resource types (`Patient`, `AllergyIntolerance`, `Condition`, `Encounter`,
  `Immunization`, `Medication`, `MedicationRequest`, `MedicationStatement`, `Organization`,
  `Practitioner`, `PractitionerRole`, `Procedure`, `RelatedPerson`) are stamped with their AU PS
  profiles
* `Observation` resources are stamped when they can be classified as
  [smoking status](http://hl7.org.au/fhir/ps/StructureDefinition/au-ps-smokingstatus) (LOINC
  `72166-2`) or
  [pathology results](http://hl7.org.au/fhir/ps/StructureDefinition/au-ps-diagnosticresult-path)
  (`category = laboratory`)
* optional AU PS **Alerts** (LOINC `104605-1`, `Flag`) and **Patient Story** (LOINC `81338-6`,
  `DocumentReference`) sections are included when matching resources are present

> **Note:** This plugin stamps AU PS profiles and assembles an IPS-shaped document with AU section
> extensions. Full AU Core content validation (identifiers, terminology bindings, Must Support
> elements) remains the responsibility of the host server and its loaded IGs.

## Requirements

* Java 17+
* A HAPI FHIR JPA server that includes the IPS module (`hapi-fhir-jpaserver-ips`), aligned with
  HAPI FHIR **8.10.0** (or compatible)

## Building

```bash
mvn clean verify
```

The resulting `target/au-ps-generator-<version>.jar` can be installed into a HAPI FHIR server.
HAPI FHIR and Spring dependencies are declared with `provided` scope because they are supplied by
the host server at runtime.

## Getting Started

### Via Maven

The generator is published to
[GitHub Packages](https://github.com/waymarksystems/au-ps-generator/packages).

**1. Authenticate with GitHub Packages**

Add credentials to `~/.m2/settings.xml`. The `<id>` must match the repository id used below
(`github`):

```xml
<settings>
  <servers>
    <server>
      <id>github</id>
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
    <id>github</id>
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
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```

Replace `1.0.0-SNAPSHOT` with the
[latest published version](https://github.com/waymarksystems/au-ps-generator/packages).

### Via a JAR

Download the JAR from the
[GitHub Packages page](https://github.com/waymarksystems/au-ps-generator/packages) and add it to
your project classpath.

## Configuration

Enable the generator in `application.yaml` (or `application.properties`):

```yaml
hapi:
  fhir:
    aups_enabled: true
    # Avoid registering the built-in IPS strategy at the same time
    ips_enabled: false
    narrative_enabled: true
```

Or with properties syntax:

```properties
hapi.fhir.aups_enabled=true
hapi.fhir.ips_enabled=false
hapi.fhir.narrative_enabled=true
```

When enabled, the plugin auto-registers:

* `AuPsGenerationStrategy` (as `IIpsGenerationStrategy`, if none is already defined)
* `IpsGeneratorSvcImpl` (as `IIpsGeneratorSvc`, if none is already defined)
* `AuPatientSummaryGeneratorProvider` (extends `IpsOperationProvider` for `Patient/$summary`)
* `AuPatientSummaryGeneratorSvc` for programmatic generation

Leave `hapi.fhir.ips_enabled` disabled (or otherwise avoid declaring a competing
`IIpsGenerationStrategy`) so the AU PS strategy is the one used for `$summary`.

For IG validation of generated documents, load the `hl7.fhir.au.ps` package in the host server
separately — this plugin does not package the Implementation Guide.

## Narrative

Section narratives are defined in
`src/main/resources/au/ps/narrative/aups-narratives.properties` and rendered from Thymeleaf HTML
templates under `classpath:au/ps/narrative/`. The Composition narrative title is
**Australian Patient Summary**. Shared rendering helpers reuse the IPS utility fragment name
(`IpsUtilityFragments`) so section templates stay compatible with HAPI's narrative engine.

Customise a section without breaking conformance by overriding the `.narrative` entry:

```properties
aups-allergyintolerance.resourceType=Bundle
aups-allergyintolerance.profile=https://hl7.org/fhir/uv/ips/StructureDefinition-Composition-uv-ips-definitions.html#Composition.section:sectionAllergies
aups-allergyintolerance.narrative=classpath:my/custom/allergyintolerance.html
```

## Publishing

SNAPSHOT builds are published to GitHub Packages on every push to `master`. Release builds are
published for every `v*` tag.

```bash
git tag v1.0.0
git push origin v1.0.0
```

The workflow (`.github/workflows/publish.yml`) deploys using `GITHUB_TOKEN` — no additional
secrets are required for GitHub Packages.

## License

Apache License 2.0 — see [LICENSE](LICENSE).
