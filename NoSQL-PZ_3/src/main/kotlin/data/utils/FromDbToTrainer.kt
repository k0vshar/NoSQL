package data.utils

import domain.entity.users.Trainer
import java.sql.ResultSet

fun ResultSet.toUser() = Trainer(
        id = this.getInt(1).toString(),
        name = this.getString(2),
        surname = this.getString(3),
        login = this.getString(4),
        password = this.getString(5),
        phoneNumber = this.getString(6),
        email = this.getString(7)
)