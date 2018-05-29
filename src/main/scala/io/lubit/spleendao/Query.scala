package io.lubit.spleendao

import java.nio.ByteBuffer
import java.sql.{Connection, ResultSet}
import java.util.UUID

import scala.collection.immutable.List
import scala.collection.mutable.ListBuffer

object Query {

  type RowColumns = Seq[Column]
  type RowValues = Seq[Any]

  case class Column(index: Int, name: String, tableName: String, label: String, sqlType: String)

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

  def execute(sql: String, mapper: TypeMapper)(implicit connection: Connection): QueryResult = {

    val statement = connection.createStatement()
    val resultSet = statement.executeQuery(sql)
    val columns = rowColumns(resultSet)

    QueryResult(
      columns = columns,
      values = resultSetValues(resultSet, columns, mapper)
    )
  }

  def rowColumns(resultSet: ResultSet): RowColumns = {
    val rsmd = resultSet.getMetaData
    val cnt = rsmd.getColumnCount

    (1 to cnt).map { i =>
      val name = rsmd.getColumnName(i)
      val typeName = rsmd.getColumnTypeName(i)

      Column(
        index = i,
        name = name,
        tableName = rsmd.getTableName(i),
        label = rsmd.getColumnLabel(i),
        sqlType = typeName
      )
    }
  }

  def rowValues(resultSet: ResultSet, columns: RowColumns, mapper: TypeMapper): RowValues = {
    columns.map { column =>
      mapper.convert(resultSet, column)
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
