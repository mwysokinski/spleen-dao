# Spleen DAO

This is a simple JDBC wrapper written in Scala.
Project uses [dbcp2](https://commons.apache.org/proper/commons-dbcp/) connection pool and 
separate thread pool to execute statements in async way.

Disclaimer: Project is in early stage.

### Database support
For the time being Spleen supports MySQL and PostgreSQL however not all types are mapped yet.

### Defining datasource

* MySQL
```
  val config = DataSourceConfig(
    driver = "com.mysql.jdbc.Driver",
    jdbcUrl = "jdbc:mysql://localhost:3306/your_db",
    user = "user",
    password = "***",
    poolSize = 1,
    databaseType = DatabaseTypes.MySQL
  )

```

* PostgreSQL
```
  val config = DataSourceConfig(
    driver = "org.postgresql.Driver",
    jdbcUrl = "jdbc:postgresql://localhost:5432/your_db",
    user = "user",
    password = "***",
    poolSize = 1,
    databaseType = DatabaseTypes.PostgreSQL
  )

```
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

### Schema information
Retrieves info about defined tables/columns

```
    val query = ds.informationQuery("test")

    ds.withConnection { implicit connection =>
      query.values
    } foreach (_.map(_.toString).foreach(println(_)))

```


