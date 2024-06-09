package fraktal.io.android.demo.chat.domain

import fraktal.io.android.demo.shared.models.chat.ChatMessagesPart

object ChatQSE {
   data class State(
       val list : ChatMessagesPart,
       val pages: Int,
       val initLoading: Boolean,
       val nextPageLoading: Boolean,
       val messagesErrorStr: String?
   )
}