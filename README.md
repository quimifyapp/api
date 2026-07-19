# Quimify API

## Overview

The Quimify API is the backend behind [Quimify](https://quimify.com/). It resolves
chemistry queries — inorganic and organic nomenclature, molecular masses and
equation balancing — and serves them to the client over a stateless REST interface.

Built with Spring Boot (Spring MVC) on Java 11. Data is persisted through Spring Data
JPA over MySQL, which also backs a query cache so repeated lookups skip recomputation.
The organic engine is embedded via the [quimify-organic](https://github.com/quimifyapp/organic)
module, and Spring Boot Actuator exposes health and metrics.
Nomenclature is served in Spanish and English.

## Endpoints

Language is selected per request via the `language` header
(`es` / `en`, defaults to `en`).

### Inorganic

| Method | Route                       | Params            | What it does                                 |
|--------|-----------------------------|-------------------|----------------------------------------------|
| `GET`  | `/inorganic/completion`     | `input`           | Autocomplete suggestions as the user types   |
| `GET`  | `/inorganic/from-completion`| `completion`      | Resolves a picked suggestion into a result   |
| `GET`  | `/inorganic`                | `input`           | Searches a compound by name or formula       |
| `GET`  | `/inorganic/deep`           | `input`           | Slower, exhaustive search when the fast one misses |

### Organic

| Method | Route                | Params                    | What it does                          |
|--------|----------------------|---------------------------|---------------------------------------|
| `GET`  | `/organic/structure` | `name`                    | Name → structure and SMILES           |
| `GET`  | `/organic/name`      | `structure-sequence`      | Built structure → IUPAC name          |

### Tools

| Method | Route             | Params                 | What it does                          |
|--------|-------------------|------------------------|---------------------------------------|
| `GET`  | `/molecular-mass` | `formula`              | Molecular mass and per-element split  |
| `GET`  | `/equation`       | `reactants`, `products`| Balances a chemical equation          |

### Client & reporting

| Method | Route           | Params                                            | What it does                          |
|--------|-----------------|---------------------------------------------------|---------------------------------------|
| `GET`  | `/client`       | `platform`, `version`                             | Client config and update info         |
| `POST` | `/report`       | `context`, `details`, `user-message`, `client-version` | Stores a user-submitted report   |
| `POST` | `/client-error` | `context`, `details`, `client-version`            | Logs a client-side error              |
| `GET`  | `/health`       | —                                                 | Service status                        |

## How it works

![flowchart](doc/inorganic-suggestions.png?raw=true "Flowchart")

## Usage

Clone the repository and run it with Maven (Java 11):

```bash
mvn spring-boot:run
```

It expects a MySQL database (see `sql/`) and the usual Spring configuration.

## Dependencies

Quimify API uses the `quimify-organic` module from [quimifyapp/organic](https://github.com/quimifyapp/organic).

## License?

Do whatever you want with this code.

However, please be aware that our code relies on open-source dependencies, each with their own licenses and usage terms. Non-compliance with their licenses could have legal consequences.
