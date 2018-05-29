package io.lubit.spleendao

trait DatabaseType

object DatabaseTypes {

  case object MySQL extends DatabaseType

  case object PostgreSQL extends DatabaseType

}
