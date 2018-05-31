package io.lubit.spleendao.db.postgresql

import java.sql.ResultSet

import io.lubit.spleendao.ColumnMapper.DefaultColumnMapper
import io.lubit.spleendao.Query.RowColumns
import io.lubit.spleendao.dto.InformationSchemaDto
import io.lubit.spleendao.{InformationSchemaMapper, Query}

object PostgreSqlColumnMapper extends DefaultColumnMapper {

  override def convert(column: Query.Column)(implicit resultSet: ResultSet): Any = column.sqlType.toLowerCase match {

    case "boolean" => resultSet.getBoolean(column.index)
    case "character" => resultSet.getString(column.index)
    case "character varying" => resultSet.getString(column.index)

    case _ => resultSet.getString(column.index)

  }

}

object PostgreSqlInformationSchemaMapper extends InformationSchemaMapper {

  override def convert(resultSet: ResultSet, columns: RowColumns): InformationSchemaDto = {
    InformationSchemaDto(
      tableName = resultSet.getString(1),
      columnName = resultSet.getString(2),
      dataType = resultSet.getString(3)
    )
  }
}
