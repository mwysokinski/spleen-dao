package io.lubit.spleendao

import java.sql.{Connection, PreparedStatement, ResultSet}

import io.lubit.spleendao.Query._

import scala.collection.immutable.List
import scala.collection.mutable.ListBuffer

case class Query[T](sql: String, mapper: RowMapper[T], params: Option[Params] = None) {

  def result(implicit connection: Connection): QueryResult[T] = {
    val resultSet = statement.executeQuery()
    val columns = rowColumns(resultSet)

    QueryResult(
      columns = columns,
      values = resultSetValues(resultSet, columns, mapper)
    )
  }

  def values(implicit connection: Connection): List[T] = {
    val resultSet = statement.executeQuery()
    val columns = rowColumns(resultSet)

    resultSetValues(resultSet, columns, mapper)
  }

  def executeUpdate(implicit connection: Connection): Int = statement.executeUpdate

  def withParams(params: Params): Query[T] = copy(params = Some(params))

  def withParams(params: (String, Any)*): Query[T] = copy(params = Some(Map(params: _*)))

  def withMapper[S](mapper: RowMapper[S]): Query[S] = copy(mapper = mapper)

  def statement(implicit connection: Connection) = {
    val sqlNoParams = sql.replaceAll(ParamsPattern.regex, "?")
    val statement = connection.prepareStatement(sqlNoParams)

    params.foreach { params =>
      ParamsPattern.findAllMatchIn(sql).zipWithIndex.foreach { case (param, index) =>
        params.get(param.group(1)).foreach { value =>
          setParam(statement, index + 1, value)
        }
      }
    }

    statement
  }

}

object Query {

  type RowColumns = Seq[Column]
  type RowValues = Seq[Any]
  type Params = Map[String, Any]

  val ParamsPattern = "\\:([\\w]+)".r

  def setParam(statement: PreparedStatement, index: Int, value: Any) = value match {
    case i: Int => statement.setInt(index, i)
    case other => statement.setString(index, other.toString)
  }


  case class Column(index: Int,
                    name: String,
                    tableName: String,
                    label: String,
                    sqlType: String,
                    nullable: Boolean,
                    precision: Long)

  case class QueryResult[T](columns: RowColumns, values: List[T]) {

    def print: Unit = {
      println("--------------------------------")
      val cols = columns.map { col =>
        s"[${col.tableName}] ${col.label} (${col.sqlType})"
      } mkString ("|")
      println(cols)
      println("--------------------------------")

      values.foreach { row =>
        println(row)
      }

      println("--------------------------------")
    }

  }


  def rowColumns(resultSet: ResultSet): RowColumns = {
    val rsmd = resultSet.getMetaData
    val cnt = rsmd.getColumnCount

    (1 to cnt).map { i =>
      Column(
        index = i,
        name = rsmd.getColumnName(i),
        tableName = rsmd.getTableName(i),
        label = rsmd.getColumnLabel(i),
        sqlType = rsmd.getColumnTypeName(i),
        nullable = rsmd.isNullable(i) == 1,
        precision = rsmd.getPrecision(i)
      )
    }
  }


  def resultSetValues[T](resultSet: ResultSet, columns: RowColumns, mapper: RowMapper[T]): List[T] = {
    val result = new ListBuffer[T]

    while (resultSet.next()) {
      result += mapper.convert(resultSet, columns)
    }

    result.toList
  }

}
