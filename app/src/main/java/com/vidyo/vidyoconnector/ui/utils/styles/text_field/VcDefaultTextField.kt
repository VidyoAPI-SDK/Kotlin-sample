package com.vidyo.vidyoconnector.ui.utils.styles.text_field

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation

object VcDefaultTextField {
    @Composable
    fun defaultGrayColors(): TextFieldColors {
        val colors = MaterialTheme.colors
        return TextFieldDefaults.textFieldColors(
            textColor = Color.White,
            backgroundColor = Color(0xFFAFAFAF),

            focusedLabelColor = colors.primary,
            unfocusedLabelColor = Color(0xFFDDDDDD),
            disabledLabelColor = Color.DarkGray,

            placeholderColor = Color(0xFFDDDDDD),
            disabledPlaceholderColor = Color.White,

            errorIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        )
    }

    @Composable
    fun defaultWhiteColors(): TextFieldColors {
        val colors = MaterialTheme.colors
        return TextFieldDefaults.textFieldColors(
            textColor = Color.Black,
            backgroundColor = Color.White,

            focusedLabelColor = colors.primary,
            unfocusedLabelColor = Color(0xFFDDDDDD),
            disabledLabelColor = Color.DarkGray,

            placeholderColor = Color(0xFFDDDDDD),
            disabledPlaceholderColor = Color.White,

            errorIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        )
    }
}

@Composable
fun VcDefaultTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = MaterialTheme.shapes.small,
    colors: TextFieldColors = VcDefaultTextField.defaultGrayColors(),
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        interactionSource = interactionSource,
        shape = shape,
        colors = colors,
    )
}
