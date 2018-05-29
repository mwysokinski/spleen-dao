package io.lubit.spleendao

import java.sql.Connection
import java.util.concurrent.{Executors, TimeUnit}

import org.apache.commons.dbcp2.BasicDataSource
import DataSource._
import io.lubit.spleendao.Modification.Params
import io.lubit.spleendao.mappers.TypeMapper

import scala.concurrent.{ExecutionContext, Future}

object DataSource {

  case class DataSourceConfig(driver: String,
                              jdbcUrl: String,
                              user: String,
                              password: String,
                              poolSize: Int,
                              databaseType: DatabaseType)

  class ConnectionPool(config: DataSourceConfig) extends BasicDataSource {
    this.setUsername(config.user)
    this.setPassword(config.password)
    this.setDriverClassName(config.driver)
    this.setUrl(config.jdbcUrl)
    this.setInitialSize(config.poolSize)
  }

}

class DataSource(config: DataSourceConfig) {

  private val pool = new ConnectionPool(config)

  private implicit val ec = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(config.poolSize))

  private implicit val mapper = TypeMapper(config.databaseType)

  def withConnection[T](body: Connection => T): Future[T] = Future {
    val connection = pool.getConnection
    val res = body(connection)
    connection.close()
    res
  }

  def execute(sql: String)(implicit connection: Connection) = Query.execute(sql)

  def execute(sql: String, params: Params)(implicit connection: Connection): Int = Modification.execute(sql, params)

  def shutdown: Unit = {
    Thread.sleep(1000)
    ec.shutdown()
    ec.awaitTermination(30, TimeUnit.SECONDS)
    pool.close()
  }


}
