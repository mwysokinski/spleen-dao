package io.lubit.spleendao

import java.sql.Connection
import java.util.concurrent.Executors

import org.apache.commons.dbcp2.BasicDataSource
import DataSource._

import scala.concurrent.{ExecutionContext, Future}

object DataSource {

  case class DataSourceConfig(driver: String, jdbcUrl: String, user: String, password: String, size: Int)

  class ConnectionPool(config: DataSourceConfig) extends BasicDataSource {
    this.setUsername(config.user)
    this.setPassword(config.password)
    this.setDriverClassName(config.driver)
    this.setUrl(config.jdbcUrl)
    this.setInitialSize(config.size)
  }

}

class DataSource(config: DataSourceConfig) {

  private val pool = new ConnectionPool(config)

  implicit val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(config.size))

  def withConnection[T](body: Connection => T): Future[T] = Future {
    val connection = pool.getConnection
    val res = body(connection)
    connection.close()
    res
  }


}
