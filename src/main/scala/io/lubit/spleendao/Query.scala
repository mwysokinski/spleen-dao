package io.lubit.spleendao

import java.sql.{Connection, PreparedStatement, ResultSet}

import io.lubit.spleendao.Query.Params

import scala.collection.immutable.List
import scala.collection.mutable.ListBuffer

case class Query(sql: String, mapper: TypeMapper, params: Option[Params] = None) {

  def execute(implicit connection: Connection) = Query.execute(this)

  def executeUpdate(implicit connection: Connection) = Query.executeUpdate(this)

  def withParams(params: Params): Query = copy(params = Some(params))

  def withParams(params: (String, Any)*): Query = copy(params = Some(Map(params: _*)))
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

  case class QueryResult(columns: RowColumns, values: List[RowValues]) {

    def print: Unit = {
      println("--------------------------------")
      val cols = columns.map { col =>
        s"[${col.tableName}] ${col.label} (${col.sqlType})"
      } mkString ("|")
      println(cols)
      println("--------------------------------")

      values.foreach { row =>
        println(row.mkString("|"))
      }

      println("--------------------------------")
    }

  }

  def execute(query: Query)(implicit connection: Connection): QueryResult = {
    val resultSet = statement(query).executeQuery()
    val columns = rowColumns(resultSet)

    QueryResult(
      columns = columns,
      values = resultSetValues(resultSet, columns, query.mapper)
    )
  }

  def executeUpdate(query: Query)(implicit connection: Connection): Int = {
    val res = statement(query).executeUpdate()
    connection.close()
    res
  }

  def statement(query: Query)(implicit connection: Connection) = {
    val sqlNoParams = query.sql.replaceAll(ParamsPattern.regex, "?")
    val statement = connection.prepareStatement(sqlNoParams)

    query.params.foreach { params =>
      ParamsPattern.findAllMatchIn(query.sql).zipWithIndex.foreach { case (param, index) =>
        params.get(param.group(1)).foreach { value =>
          setParam(statement, index + 1, value)
        }
      }
    }

    statement
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

  def wrapResult(resultSet: ResultSet, column: Query.Column, result: Any): Any = {
    if (column.nullable) {
      if (resultSet.wasNull()) None else Some(result)
    } else {
      result
    }
  }

  def rowValues(resultSet: ResultSet, columns: RowColumns, mapper: TypeMapper): RowValues = {
    columns.map { column =>
      wrapResult(resultSet, column, mapper.convert(resultSet, column))
    }
  }


  def resultSetValues(resultSet: ResultSet, columns: RowColumns, mapper: TypeMapper): List[RowValues] = {
    val result = new ListBuffer[RowValues]

    while (resultSet.next()) {
      result += rowValues(resultSet, columns, mapper)
    }

    result.toList
  }

}
