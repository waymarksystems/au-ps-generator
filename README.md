# AU Patient Summary (AU PS) Generator

In FHIR, a Patient Summary (IPS et al) is expressed as a FHIR Document. The HAPI FHIR JPA server supports the automated generation of Patient Summary documents via the $summary operation.

This project is a plugin for the HAPI FHIR JPA server that implements the [AU Patient Summary FHIR IG](https://github.com/hl7au/au-fhir-ps) and generates an AU Patient Summary document.

## Getting Started


## Configuration


## Narrative

This generator includes a basic narrative generator for each of the section types within the Patient Summary document. This narrative can be customised to suit your needs without breaking the conformance of the patient summary.

Example:
```
aups-allergyintolerance.resourceType=Bundle
aups-allergyintolerance.profile=https://hl7.org/fhir/uv/ips/StructureDefinition-Composition-uv-ips-definitions.html#Composition.section:sectionAllergies
aups-allergyintolerance.narrative=classpath:ca/uhn/fhir/jpa/ips/narrative/allergyintolerance.html
```


