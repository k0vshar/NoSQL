package data.utils

import org.intellij.lang.annotations.Language

object TrainersTable {

    @Language("sql")
    const val GET_NUMBER_OF_CLIENTS = "{?= call get_number_of_clients(?)}"

    @Language("sql")
    const val GET_ALL = "select * from trainer order by id limit ?, ?"

    @Language("sql")
    const val GET_BY_ID = "select * from trainer where id = ?"

    @Language("sql")
    const val GET_BY_NAME_AND_SURNAME_OR_LOGIN = "select * from trainer where t_name = ? and surname = ? or login = ? order by id limit ?, ?"

    @Language("sql")
    const val GET_TRAINER_BY_CLIENT_ID = "select * from trainer where id = (select trainer_id from client where id = ?)"

    @Language("sql")
    const val GET_BY_NAME_AND_SURNAME = "select * from trainer where t_name = ? and surname = ?"

    @Language("sql")
    const val SAVE = "insert into trainer (t_name, surname, login, t_password, phone_number, email, id) values (?, ?, ?, ?, ?, ?, ?)"

    @Language("sql")
    const val UPDATE = "update trainer set t_name = ?, surname = ?, login = ?, t_password = ?, phone_number = ?, email = ? where id = ?"

    @Language("sql")
    const val DELETE = "delete from trainer where id = ?"
}