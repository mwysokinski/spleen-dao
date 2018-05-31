# Spleen DAO

This is a simple data access object written in Scala, using JDBC.
Project uses [dbcp2](https://commons.apache.org/proper/commons-dbcp/) connection pool and 
separate thread pool to execute statements in async way.

Disclaimer: Project is in early stage.

### Query (select) usage
```
    val ds = new DataSource(config)
    
    val query = ds.query(
      """
        |SELECT *
        |FROM test.user
        |limit 10
      """.stripMargin)
      

    ds.withConnection { implicit connection =>
      query.result
    } foreach (res => res.print)

```

### Query with params (select) usage
```
    val ds = new DataSource(config)
    
    val query = ds.query(
      """
        |SELECT *
        |FROM test.user
        |WHERE email like :email
        |limit 10
      """.stripMargin)
      

    ds.withConnection { implicit connection =>
      query
        .withParams("email" -> "roberta%")
        .result
    } foreach (res => res.print)

```

### Modification (insert) usage

```
    val query = ds.query(
      """
        |INSERT INTO `test`.`user` (`id`,`name`)
        |VALUES (:id, :name)
      """.stripMargin)
      
    ds.withConnection { implicit connection =>
      query.withParams("id" -> 1, "name" -> "Marcin").executeUpdate
    } 

```

### Modification (update) usage

```
    val query = ds.query(
      """
        |UPDATE `test`.`user`
        |SET `name` = :name
        |WHERE `id` = :id;
      """.stripMargin)
      
    ds.withConnection { implicit connection =>
      query.withParams("id" -> 1, "name" -> "Roman").executeUpdate
    }

```

