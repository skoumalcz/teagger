package com.skoumal.teagger.ui

import android.util.Log
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.skoumal.teagger.Teagger
import com.skoumal.teanity.databinding.GenericRvItem
import com.skoumal.teanity.extensions.compareToSafe

sealed class LoggerLine(
    val item: String,
    val color: Int
) : GenericRvItem() {

    private class Assert(line: String) : LoggerLine(line, R.color.logger_color_assert)
    private class Debug(line: String) : LoggerLine(line, R.color.logger_color_debug)
    private class Error(line: String) : LoggerLine(line, R.color.logger_color_error)
    private class Info(line: String) : LoggerLine(line, R.color.logger_color_info)
    private class Verbose(line: String) : LoggerLine(line, R.color.logger_color_verbose)
    private class Warn(line: String) : LoggerLine(line, R.color.logger_color_warn)
    private class Default(line: String) : LoggerLine(line, R.color.logger_color_default)

    override val layoutRes = R.layout.item_logger_line

    override fun contentSameAs(other: GenericRvItem) =
        other.compareToSafe<LoggerLine> { it.color == color }

    override fun sameAs(other: GenericRvItem) =
        other.compareToSafe<LoggerLine> { it.item == item }

    companion object {

        @JvmStatic
        @JvmName("withLine")
        operator fun invoke(
            line: String
        ) = when (Teagger.instance.entryTransformer.resolvePriorityForEntry(line)) {
            Log.ASSERT -> Assert(line)
            Log.DEBUG -> Debug(line)
            Log.ERROR -> Error(line)
            Log.INFO -> Info(line)
            Log.VERBOSE -> Verbose(line)
            Log.WARN -> Warn(line)
            else -> Default(line)
        }

    }

}

@BindingAdapter("textColorRes")
fun TextView.setTextColorResource(resId: Int) {
    setTextColor(ContextCompat.getColor(context, resId))
}