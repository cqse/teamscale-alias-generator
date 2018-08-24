# Teamscale Alias Generator [![Build Status](https://travis-ci.com/cqse/teamscale-alias-generator.svg?branch=master)](https://travis-ci.com/cqse/teamscale-alias-generator)
User Alias generation Tool for Teamscale

## Download

[GitHub Releases](https://github.com/cqse/teamscale-alias-generator/releases)

## Usage

### Command Line Parameters

```
  * -c, --config
      Sets the path to the alias config file.
    -d, --dry
      Enables dry-run.
      Default: false
    -e, --email
      Adds the email addresses as alias.
      Default: false
    -h, --help

  * -s, --server-url
      Sets the url of the server.
  * -t, --token
      Sets the user token.
  * -u, --user
      Sets the server user.
```
### Alias Configuration File

```
[
  {
    match: "(\\w+\\d+)",
    replace: "DOMAIN\\\\$1",
    caseSensitive: true
  }
]
```

## Development

Import in Eclipse as Gradle project using the Gradle Buildship plugin.
