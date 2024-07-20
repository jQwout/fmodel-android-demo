package fraktal.io.android.demo.theme

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import fraktal.io.android.demo.shared.utils.Validation.isValidDateFromString

object DateTransformation : VisualTransformation {

    const val MAX_DATE_SIZE = 8

    override fun filter(text: AnnotatedString): TransformedText {
        return dateFilter(text)
    }

    private fun dateFilter(text: AnnotatedString): TransformedText {

        val numberOffsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 1) return offset
                if (offset <= 3) return offset + 1
                if (offset <= 8) return offset + 2
                return 10
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 5) return offset - 1
                if (offset <= 10) return offset - 2
                return 8
            }
        }

        if (isValidDateFromString(text.text)) {
            return TransformedText(AnnotatedString(text.text), numberOffsetTranslator)
        }

        val trimmed = if (text.text.length >= MAX_DATE_SIZE) text.text.substring(0..<MAX_DATE_SIZE) else text.text

        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i % 2 == 1 && i < 4) out += "/"
        }

        return TransformedText(AnnotatedString(out), numberOffsetTranslator)
    }

}