package com.solarabehety.imagesearch.ui.activities

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.solarabehety.imagesearch.R

@Composable
internal fun SearchStatusScreen(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    status: TextStatus = TextStatus.NORMAL,
) {
    val titleColor = if (status == TextStatus.ERROR) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.primary
    }

    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        StatusImage(status)

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = titleColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.tertiary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
private fun StatusImage(status: TextStatus) {
    val imageRes = if (status == TextStatus.ERROR)
        R.drawable.il_error
    else
        R.drawable.il_empty

    Image(
        painter = painterResource(id = imageRes),
        contentDescription = null,
        modifier = Modifier
            .size(180.dp)
            .aspectRatio(1f)
    )
}

@Preview(showBackground = true)
@Composable
fun SearchStatusScreenPreview() {
    SearchStatusScreen(
        title = "No results found",
        description = "Search other",
        status = TextStatus.ERROR,
    )
}

internal enum class TextStatus {
    NORMAL,
    ERROR,
}