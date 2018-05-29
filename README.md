# Spleen DAO

This is a simple data access object written in Scala, using JDBC.
Project uses [dbcp2](https://commons.apache.org/proper/commons-dbcp/) connection pool and 
separate thread pool to execute statements in async way.

Disclaimer: Project is in early stage.

### Query (select) usage
```
    val ds = new DataSource(config)
    
    val sql =
      """
        |SELECT *
        |FROM test.user
        |limit 10
      """.stripMargin
      

    ds.withConnection { implicit connection =>
      Query.execute(sql, MySqlTypeMapper)
    } foreach (res => res.print)

```

### Modification (insert) usage

```
    val sql =
      """
        |INSERT INTO `test`.`user` (`id`,`name`)
        |VALUES (:id, :name)
      """.stripMargin
      

    ds.withConnection { implicit connection =>
      val params = Map("id" -> 1, "name" -> "Marcin")
      Modification.execute(sql, params)
    } 

```

### Modification (update) usage

```
    val sql =
      """
        |UPDATE `test`.`user`
        |SET `name` = :name
        |WHERE `id` = :id;
      """.stripMargin
      

    ds.withConnection { implicit connection =>
      val params = Map("id" -> 1, "name" -> "Roman")
      Modification.execute(sql, params)
    }

```

