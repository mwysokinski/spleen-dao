package io.lubit.spleendao.db.mysql

import io.lubit.spleendao.{DbSpecificQueries, Query}

object MySqlQueries extends DbSpecificQueries {

  def query(sql: String) = Query(sql, MySqlTypeMapper)

  override def informationQuery(schema: String): Query = query(
    """
      |SELECT `table_name`, `column_name`, `data_type`
      |FROM INFORMATION_SCHEMA.COLUMNS
      |WHERE `table_schema` = :schema
      |ORDER BY `table_name`, `ORDINAL_POSITION`
  """.stripMargin) withParams ("schema" -> schema)

}
