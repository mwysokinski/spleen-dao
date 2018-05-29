package io.lubit.spleendao.mappers

import java.sql.ResultSet

import io.lubit.spleendao.Query

object MySqlTypeMapper extends TypeMapper {

  val BinaryToUUID = true

  override def convert(resultSet: ResultSet, column: Query.Column): Any = column.sqlType match {
    case "BINARY" =>
      val res = resultSet.getBytes(column.index)

      // uuid
      if (BinaryToUUID && !resultSet.wasNull() && res.length == 16) fromByteArray(res) else res

    case "INT" | "INT UNSIGNED" => resultSet.getInt(column.index)

    case "TINYINT" => resultSet.getBoolean(column.index)

    case "DECIMAL UNSIGNED" | "DECIMAL" => resultSet.getBigDecimal(column.index)

    case "DATE" | "DATETIME" => resultSet.getDate(column.index)

    case "TIME" => resultSet.getTime(column.index)

    case "TIMESTAMP" => resultSet.getTimestamp(column.index)

    case "VARCHAR" => resultSet.getString(column.index)

    case _ => resultSet.getString(column.index)

  }
}
