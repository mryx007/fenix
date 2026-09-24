/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.home.topsites.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import mozilla.components.compose.base.annotation.FlexibleWindowLightDarkPreview
import mozilla.components.feature.top.sites.TopSite
import org.mozilla.fenix.home.fake.FakeHomepagePreview
import org.mozilla.fenix.home.topsites.TOP_SITES_ITEM_SIZE
import org.mozilla.fenix.home.topsites.TopSiteColors
import org.mozilla.fenix.home.topsites.TopSiteItem
import org.mozilla.fenix.home.topsites.getMenuItems
import org.mozilla.fenix.home.topsites.interactor.TopSiteInteractor
import org.mozilla.fenix.theme.FirefoxTheme

@Composable
internal fun Shortcuts(
    topSites: List<TopSite>,
    interactor: TopSiteInteractor,
    topSiteColors: TopSiteColors = TopSiteColors.colors(),
    showAddShortcut: Boolean = false,
    onAddShortcutClicked: () -> Unit,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val cardSize = org.mozilla.fenix.custom.CustomTopSitesSize.getSize(context)
    val spacing = org.mozilla.fenix.custom.CustomTopSitesSize.getSpacing(context)
    val fontSize = org.mozilla.fenix.custom.CustomTopSitesSize.getFontSize(context)
    val itemSize = (cardSize + spacing + 24).coerceIn(56, 96)
    val gap = (spacing / 4).coerceAtLeast(2)

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = itemSize.dp),
        verticalArrangement = Arrangement.spacedBy(gap.dp),
        horizontalArrangement = Arrangement.spacedBy(gap.dp),
        modifier = Modifier.padding(16.dp),
    ) {
        topSites.forEachIndexed { position, topSite ->
            item {
                val canMoveLeft = position > 0
                val canMoveRight = position < topSites.lastIndex
                TopSiteItem(
                    topSite = topSite,
                    menuItems =
                        getMenuItems(
                            topSite = topSite,
                            onOpenInPrivateTabClicked = interactor::onOpenInPrivateTabClicked,
                            onEditTopSiteClicked = interactor::onEditTopSiteClicked,
                            onRemoveTopSiteClicked = interactor::onRemoveTopSiteClicked,
                            onSettingsClicked = interactor::onSettingsClicked,
                            onSponsorPrivacyClicked = interactor::onSponsorPrivacyClicked,
                            canMoveLeft = canMoveLeft,
                            canMoveRight = canMoveRight,
                            onMoveTopSiteClicked = interactor::onMoveTopSiteClicked,
                        ),
                    position = position,
                    topSiteColors = topSiteColors,
                    onTopSiteClick = { topSite ->
                        interactor.onSelectTopSite(
                            topSite = topSite,
                            position = topSites.indexOf(topSite),
                        )
                    },
                    onTopSiteLongClick = interactor::onTopSiteLongClicked,
                    onTopSiteImpression = interactor::onTopSiteImpression,
                    onTopSitesItemBound = {},
                    cardSize = cardSize,
                    itemSize = itemSize,
                    fontSize = fontSize,
                )
            }
        }

        if (showAddShortcut) {
            item {
                AddShortcutItem(
                    topSiteColors = topSiteColors,
                    onClick = onAddShortcutClicked,
                    cardSize = cardSize,
                    itemSize = itemSize,
                    fontSize = fontSize,
                )
            }
        }
    }
}

@Composable
@FlexibleWindowLightDarkPreview
private fun ShortcutsPreview(@PreviewParameter(ShortcutsPreviewParameterProvider::class) showAddShortcut: Boolean) {
    FirefoxTheme {
        Surface {
            Shortcuts(
                topSites = FakeHomepagePreview.topSites(),
                interactor = FakeHomepagePreview.topSitesInteractor,
                showAddShortcut = showAddShortcut,
                onAddShortcutClicked = {},
            )
        }
    }
}

private class ShortcutsPreviewParameterProvider : PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean> = sequenceOf(false, true)
}
