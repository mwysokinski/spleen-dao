package io.lubit.spleendao

import java.nio.ByteBuffer
import java.sql.ResultSet
import java.util.UUID

import io.lubit.spleendao.ColumnMapper.DefaultColumnMapper
import io.lubit.spleendao.Query.{RowColumns, RowValues}
import io.lubit.spleendao.db.mysql.MySqlColumnMapper
import io.lubit.spleendao.dto.InformationSchemaDto

trait ColumnMapper[T] {
  def convert(resultSet: ResultSet, column: Query.Column): T

  def fromByteArray(array: Array[Byte]): UUID = {
    val bb: ByteBuffer = ByteBuffer.wrap(array)
    new UUID(bb.getLong(), bb.getLong())
  }

}

trait RowMapper[T] {
  def convert(resultSet: ResultSet, columns: RowColumns): T
}

trait InformationSchemaMapper extends RowMapper[InformationSchemaDto]

class DefaultRowMapper(mapper: DefaultColumnMapper) extends RowMapper[RowValues] {

  def wrapResult(resultSet: ResultSet, column: Query.Column, result: Any): Any = {
    if (column.nullable) {
      if (resultSet.wasNull()) None else Some(result)
    } else {
      result
    }
  }

  override def convert(resultSet: ResultSet, columns: RowColumns): RowValues = {
    columns.map { column =>
      wrapResult(resultSet, column, mapper.convert(resultSet, column))
    }

  }
}

object ColumnMapper {

  type DefaultColumnMapper = ColumnMapper[Any]

  def apply(databaseType: DatabaseType): DefaultColumnMapper = databaseType match {
    case DatabaseTypes.MySQL => MySqlColumnMapper

    case other => throw new Exception(s"DefaultColumnMapper for ${other} not defined!")
  }

}
