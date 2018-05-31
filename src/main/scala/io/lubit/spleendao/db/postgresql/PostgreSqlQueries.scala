package io.lubit.spleendao.db.postgresql

import io.lubit.spleendao.dto.InformationSchemaDto
import io.lubit.spleendao.{DbSpecificQueries, DefaultRowMapper, Query}

object PostgreSqlQueries extends DbSpecificQueries {

  val DefaultMapper = new DefaultRowMapper(PostgreSqlColumnMapper)

  def query(sql: String) = Query(sql, DefaultMapper)

  override def informationQuery(schema: String): Query[InformationSchemaDto] = query(
    """
      |SELECT table_name, column_name, data_type, is_nullable
      |FROM information_schema.columns
      |WHERE table_catalog=:catalog
      |AND table_schema='public'
      |ORDER BY table_name, ordinal_position
    """.stripMargin
  ) withParams ("catalog" -> schema) withMapper PostgreSqlInformationSchemaMapper

}
