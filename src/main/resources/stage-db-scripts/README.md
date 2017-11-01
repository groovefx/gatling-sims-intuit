## Migrating Cassandra Schema
This project supports Cassandra Schema migrations similar to [Flyway](https://flywaydb.org/). This includes migrating table schema as well as `Solr` schema migrations.

Similar to tables, Solr migrations are managed by invoking `cql` scripts. This leverages the Solr search schema alterations supported by CQL version 5.1 and above. More details [here](https://docs.datastax.com/en/dse/5.1/cql/cql/cql_reference/cql_commands/cqlAlterSearchIndexSchema.html).

## Instructions
For making any changes, create a new file with a '.cql' extension. The file name should follow this pattern. More details [here](https://github.com/patka/cassandra-migration)
```bash
<version>_<name>.cql
``` 


