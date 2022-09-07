package com.skoumal.teagger.comms

import com.skoumal.teagger.entry.LogEntryDelegate
import com.skoumal.teanity.component.channel.Vessel
import com.skoumal.teanity.tools.annotation.SubjectsToChange
import kotlinx.coroutines.FlowPreview

@UseExperimental(SubjectsToChange::class, FlowPreview::class)
sealed class EntrySailor : Vessel.Sailor {

    data class Throwee(
        val throwable: Throwable
    ) : EntrySailor()

    data class Log(
        val priority: Int,
        val tag: String,
        val message: String
    ) : EntrySailor()

    fun format(entryTransformer: LogEntryDelegate): String {
        return when (this) {
            is Throwee -> entryTransformer.entryFor(throwable)
            is Log -> entryTransformer.entryFor(priority, tag, message)
        }
    }

}