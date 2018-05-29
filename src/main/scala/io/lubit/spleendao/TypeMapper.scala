package io.lubit.spleendao

import java.sql.ResultSet

import Query.fromByteArray

trait TypeMapper {
  def convert(resultSet: ResultSet, column: Query.Column): Any
}


object MySqlTypeMapper extends TypeMapper {

  override def convert(resultSet: ResultSet, column: Query.Column): Any = column.sqlType match {
    case "BINARY" =>
      val res = resultSet.getBytes(column.index)
      if (!resultSet.wasNull() && res.length == 16) fromByteArray(res) else res // uuid

    case _ => resultSet.getString(column.index)

  }
}
