package io.lubit.spleendao

import java.sql.{Connection, PreparedStatement}

object Modification {

  type Params = Map[String, Any]

  val ParamsPattern = "\\:([\\w]+)".r

  def setParam(statement: PreparedStatement, index: Int, value: Any) = value match {
    case i: Int => statement.setInt(index, i)
    case other => statement.setString(index, other.toString)
  }

  def execute(sql: String, params: Params)(implicit connection: Connection): Int = {


    val sqlNoParams = sql.replaceAll(ParamsPattern.regex, "?")
    val statement = connection.prepareStatement(sqlNoParams)

    ParamsPattern.findAllMatchIn(sql).zipWithIndex.foreach { case (param, index) =>
      params.get(param.group(1)).foreach { value =>
        setParam(statement, index + 1, value)
      }
    }

    val res = statement.executeUpdate()
    connection.close()
    res
  }


}
