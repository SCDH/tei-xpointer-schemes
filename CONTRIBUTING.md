#### Release policy

Release numbers follow the `MAJOR.MINOR.BUGFIX` scheme. Releases are
automated in the CI/CD pipeline and are triggered by pushing a tag
with a name matching the scheme.


##### Supported oXygen Versions

Each major oXygen release depends on a specific Saxon
release. Unfortunately, that means that the plugin has to be build
against such Saxon versions and Oxygen SDK versions. The main
[`pom.xml`](pom.xml) file provides build profiles for Oxygen releases,
that set dependencies. They can be activated by setting the
`oxygen.version` property to a major release, `24`, `25`. To
support a new oxygen version, a new build profile has to be added and
the `oxygen.versions` property has to be updated, too. E.g. build for
Oxygen 24 run

```{shell}
mvn -Doxygen.version=24 clean package
```

The `oxygen.versions` property has the form

```
MAJOR_VERSION_PLUGIN:SUPPORTED_VERSIONS[|MAJOR_VERSION:SUPPORTED_VERSIONS]*
```

where `SUPPORTED_VERSIONS` is a comma separated list of

```
MAJOR_VERSION.MINOR_VERSION[+]
```

and `MAJOR_VERSION_PLUGIN` is

```
MAJOR_VERSION[.MINOR_VERSION]
```

For each `MAJOR_VERSION_PLUGIN` there has to be a **build profile** and
**release rule** for the [CI/CD
pipeline](.github/workflows/release.yml). So, each supported oxygen
major version has needs an entry in `pom.xml` and in the pipeline file.
