package io.lubit.spleendao

import io.lubit.spleendao.db.mysql.MySqlQueries
import io.lubit.spleendao.dto.InformationSchemaDto

trait DbSpecificQueries {

  def informationQuery(schema: String): Query[InformationSchemaDto]

}

object DbSpecificQueries {

  def apply(databaseType: DatabaseType): DbSpecificQueries = databaseType match {
    case DatabaseTypes.MySQL => MySqlQueries

    case other => throw new Exception(s"DbSpecificQueries for ${other} not defined!")
  }

}
