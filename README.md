[Table of contents generated using this app](https://tableofcontents.herokuapp.com)

- [Introduction](#introduction)
  - [Goals](#goals)
  - [Motivation](#motivation)
  - [License and origin acknowledgement](#license-and-origin-acknowledgement)
- [Resultant Database](#resultant-database)
  - [API-s](#api-s)
- [Databse Deployment](#databse-deployment)
  - [Database repilicas](#database-repilicas)
  - [UI deployments](#ui-deployments)
- [Database and packaging software developer instructions](#database-and-packaging-software-developer-instructions)
  - [Links to general comments](#links-to-general-comments)

# Introduction
## Goals
  * Get dictionaries served from [DSAL](http://dsal.uchicago.edu/dictionaries).
  * Present it in a format better suited for consumption by the broader Sanskrit programming community.

## Motivation
This database copy aims to augment DCS web interface and address the following shortcomings:
  * __Unblock NLP work__: People enthusiastic about applying machine learning tools to Sanskrit language data are hampered by lack of access to such data in a convenient format. Examples: [201706](https://groups.google.com/d/msg/sanskrit-programmers/2uwvGmrfI68/Pt8hMB3XAAAJ), [201607](https://groups.google.com/forum/#!searchin/sanskrit-programmers/DCS|sort:relevance/sanskrit-programmers/Zdj80IzI--U/G-zJEXgYCAAJ), [201605](https://groups.google.com/forum/#!searchin/sanskrit-programmers/DCS|sort:relevance/sanskrit-programmers/GMDUKF7zCaM/bOAAnNdkCQAJ)
  * __Unblock UI work__: The valuable analysis could be presented to end users in many more creative ways.

## License and origin acknowledgement
This builds on the foundational work by the DSAL team. See [LICENSE](LICENSE.md) file.

# Resultant Dicts
* Will be placed in the appropriate repositories named stardict-marathi etc..

# Database and packaging software developer instructions
## Links to general comments
See [indic-transliteration/README](https://github.com/sanskrit-coders/indic-transliteration/blob/master/README.md) for the following info:

  - [Setup](https://github.com/sanskrit-coders/indic-transliteration/blob/master/README.md#setup)
  - [Deployment](https://github.com/sanskrit-coders/indic-transliteration/blob/master/README.md#deployment)
    - [Regarding **maven targets** in intellij](https://github.com/sanskrit-coders/indic-transliteration/blob/master/README.md#regarding-**maven-targets**-in-intellij)
    - [Releasing to maven.](https://github.com/sanskrit-coders/indic-transliteration/blob/master/README.md#releasing-to-maven.)
    - [Building a jar.](https://github.com/sanskrit-coders/indic-transliteration/blob/master/README.md#building-a-jar.)
  - [Technical choices](https://github.com/sanskrit-coders/indic-transliteration/blob/master/README.md#technical-choices)
    - [Scala](https://github.com/sanskrit-coders/indic-transliteration/blob/master/README.md#scala)
