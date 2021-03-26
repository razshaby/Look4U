package com.razchen.look4u

import com.razchen.look4u.MemberData

class Message (var text: String, var memberData: MemberData, var belongsToCurrentUser: Boolean) {

    fun isBelongsToCurrentUser() : Boolean {
        return this.belongsToCurrentUser
    }
}