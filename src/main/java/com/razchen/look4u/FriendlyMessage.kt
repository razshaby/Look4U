package com.razchen.look4u

import com.google.firebase.database.IgnoreExtraProperties
import java.util.*


@IgnoreExtraProperties
data class FriendlyMessage(
        var text: String? = "",
        var name: String? = "",
        var fromUserId : String? = "",
        var userImageUrl : String? = "",
        var timeStamp : Long? = Calendar.getInstance().timeInMillis

        )

