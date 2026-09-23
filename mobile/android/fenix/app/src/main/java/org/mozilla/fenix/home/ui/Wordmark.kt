/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.home.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.mozilla.fenix.R
import org.mozilla.fenix.home.ui.HomepageTestTag.HOMEPAGE_WORDMARK_LOGO
import org.mozilla.fenix.home.ui.HomepageTestTag.HOMEPAGE_WORDMARK_TEXT

/** Semantic property for accessing a Composable item's current resource property. */
internal val ResourceId = SemanticsPropertyKey<Int>("ResourceId")
internal var SemanticsPropertyReceiver.resourceId by ResourceId

@Composable
internal fun WordmarkLogo() {
    Image(
        modifier =
            Modifier.height(36.dp)
                .semantics {
                    testTagsAsResourceId = true
                    testTag = HOMEPAGE_WORDMARK_LOGO
                    resourceId = R.attr.fenixWordmarkLogo
                }
                .padding(end = 8.dp),
        painter = painterResource(R.mipmap.ic_launcher_round),
        contentDescription = null,
    )
}

@Composable
internal fun WordmarkText(color: Color?) {
    Text(
        text = stringResource(R.string.app_name),
        color = color ?: MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
        ),
        modifier =
            Modifier.semantics {
                testTagsAsResourceId = true
                testTag = HOMEPAGE_WORDMARK_TEXT
            },
    )
}
