package io.lubit.spleendao.db.mysql

import java.sql.ResultSet

import io.lubit.spleendao.ColumnMapper.DefaultColumnMapper
import io.lubit.spleendao.Query.RowColumns
import io.lubit.spleendao.dto.InformationSchemaDto
import io.lubit.spleendao.{InformationSchemaMapper, Query}

object MySqlColumnMapper extends DefaultColumnMapper {

  val BinaryToUUID = true

  override def convert(column: Query.Column)(implicit resultSet: ResultSet): Any = {

    column.sqlType.toUpperCase match {
      case "BINARY" =>
        val res = bytes(column.index)

        // uuid
        if (BinaryToUUID && !resultSet.wasNull() && res.length == 16) fromByteArray(res) else res

      case "INT" | "INT UNSIGNED" => resultSet.getInt(column.index)

      case "TINYINT" => bool(column.index)

      case "DECIMAL UNSIGNED" | "DECIMAL" => resultSet.getBigDecimal(column.index)

      case "DATE" | "DATETIME" => resultSet.getDate(column.index)

      case "TIME" => resultSet.getTime(column.index)

      case "TIMESTAMP" => resultSet.getTimestamp(column.index)

      case "VARCHAR" => str(column.index)

      case _ => resultSet.getString(column.index)

    }
  }
}

object MySqlInformationSchemaMapper extends InformationSchemaMapper {

  override def convert(resultSet: ResultSet, columns: RowColumns): InformationSchemaDto = {
    InformationSchemaDto(
      tableName = resultSet.getString(1),
      columnName = resultSet.getString(2),
      dataType = resultSet.getString(3)
    )
  }
}
