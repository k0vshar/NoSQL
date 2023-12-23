package data.utils

import domain.entity.users.Trainer
import org.bson.types.ObjectId
import java.sql.ResultSet
import java.util.*

fun ResultSet.toUser() = Trainer(
        id = ObjectId(Date(this.getInt(1).toLong())).toHexString(),
        name = this.getString(2),
        surname = this.getString(3),
        login = this.getString(4),
        password = this.getString(5),
        phoneNumber = this.getString(6),
        email = this.getString(7)
)