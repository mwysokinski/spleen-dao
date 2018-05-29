package io.lubit.spleendao.mappers

import java.nio.ByteBuffer
import java.sql.ResultSet
import java.util.UUID

import io.lubit.spleendao.{DatabaseType, DatabaseTypes, Query}

trait TypeMapper {
  def convert(resultSet: ResultSet, column: Query.Column): Any

  def fromByteArray(array: Array[Byte]): UUID = {
    val bb: ByteBuffer = ByteBuffer.wrap(array)
    new UUID(bb.getLong(), bb.getLong())
  }

}

object TypeMapper {

  def apply(databaseType: DatabaseType): TypeMapper = databaseType match {
    case DatabaseTypes.MySQL => MySqlTypeMapper

    case other => throw new Exception(s"Mapper for ${other} not defined!")
  }
}
