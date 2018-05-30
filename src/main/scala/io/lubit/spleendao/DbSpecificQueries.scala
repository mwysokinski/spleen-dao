package io.lubit.spleendao

trait DbSpecificQueries {

  def informationQuery(schema: String): String

}
