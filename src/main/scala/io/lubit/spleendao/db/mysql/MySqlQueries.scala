package io.lubit.spleendao.db.mysql

import io.lubit.spleendao.dto.InformationSchemaDto
import io.lubit.spleendao.{DbSpecificQueries, DefaultRowMapper, Query}

object MySqlQueries extends DbSpecificQueries {

  val DefaultMapper = new DefaultRowMapper(MySqlColumnMapper)

  def query(sql: String) = Query(sql, DefaultMapper)

  override def informationQuery(schema: String): Query[InformationSchemaDto] = query(
    """
      |SELECT `table_name`, `column_name`, `data_type`
      |FROM INFORMATION_SCHEMA.COLUMNS
      |WHERE `table_schema` = :schema
      |ORDER BY `table_name`, `ORDINAL_POSITION`
  """.stripMargin
  ) withParams ("schema" -> schema) withMapper MySqlInformationSchemaMapper

}
