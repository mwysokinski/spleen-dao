package io.lubit.spleendao

import java.nio.ByteBuffer
import java.sql.ResultSet
import java.util.UUID

import io.lubit.spleendao.ColumnMapper.DefaultColumnMapper
import io.lubit.spleendao.Query.{RowColumns, RowValues}
import io.lubit.spleendao.db.mysql.MySqlColumnMapper
import io.lubit.spleendao.db.postgresql.PostgreSqlColumnMapper
import io.lubit.spleendao.dto.InformationSchemaDto

trait ColumnMapper[T] {
  def convert(column: Query.Column)(implicit resultSet: ResultSet): T

  def fromByteArray(array: Array[Byte]): UUID = {
    val bb: ByteBuffer = ByteBuffer.wrap(array)
    new UUID(bb.getLong(), bb.getLong())
  }

  @inline def bool(index: Int)(implicit resultSet: ResultSet): Boolean = resultSet.getBoolean(index)

  @inline def bytes(index: Int)(implicit resultSet: ResultSet): Array[Byte] = resultSet.getBytes(index)

  @inline def int(index: Int)(implicit resultSet: ResultSet): Int = resultSet.getInt(index)

  @inline def bigdec(index: Int)(implicit resultSet: ResultSet): BigDecimal = resultSet.getBigDecimal(index)

  @inline def date(index: Int)(implicit resultSet: ResultSet): java.sql.Date = resultSet.getDate(index)

  @inline def time(index: Int)(implicit resultSet: ResultSet): java.sql.Time = resultSet.getTime(index)

  @inline def timestamp(index: Int)(implicit resultSet: ResultSet): java.sql.Timestamp = resultSet.getTimestamp(index)

  @inline def str(index: Int)(implicit resultSet: ResultSet): String = resultSet.getString(index)

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
      wrapResult(resultSet, column, mapper.convert(column)(resultSet))
    }

  }
}

object ColumnMapper {

  type DefaultColumnMapper = ColumnMapper[Any]

  def apply(databaseType: DatabaseType): DefaultColumnMapper = databaseType match {
    case DatabaseTypes.MySQL => MySqlColumnMapper
    case DatabaseTypes.PostgreSQL => PostgreSqlColumnMapper

    case other => throw new Exception(s"DefaultColumnMapper for ${other} not defined!")
  }

}

