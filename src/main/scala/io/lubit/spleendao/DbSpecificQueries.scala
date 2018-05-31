package io.lubit.spleendao

import io.lubit.spleendao.Query.RowValues

trait DbSpecificQueries {

  def informationQuery(schema: String): Query[RowValues]

}
