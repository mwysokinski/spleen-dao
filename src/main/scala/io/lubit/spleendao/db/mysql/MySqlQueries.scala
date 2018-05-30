package io.lubit.spleendao.db.mysql

import io.lubit.spleendao.DbSpecificQueries

object MySqlQueries extends DbSpecificQueries {

  override def informationQuery(schema: String): String =
    """
      |SELECT `table_name`,`column_name`, `data_type`
      |FROM INFORMATION_SCHEMA.COLUMNS
      |WHERE `table_schema` = 'reckless'
      |ORDER BY `table_name`, `ORDINAL_POSITION`
      |
  """.stripMargin
}
