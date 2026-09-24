/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.home.topsites

import android.content.Context
import mozilla.components.feature.top.sites.TopSite
import mozilla.components.feature.top.sites.view.TopSitesView
import org.mozilla.fenix.components.AppStore
import org.mozilla.fenix.components.appstate.AppAction
import org.mozilla.fenix.custom.CustomTopSitesOrder
import org.mozilla.fenix.ext.sort
import org.mozilla.fenix.utils.Settings

class DefaultTopSitesView(
    val appStore: AppStore,
    val settings: Settings,
    val context: Context? = null,
) : TopSitesView {

    override fun displayTopSites(topSites: List<TopSite>) {
        val baseSites = if (!settings.showContileFeature) {
            topSites
        } else {
            topSites.sort()
        }
        val orderedSites = if (context != null) {
            CustomTopSitesOrder.applyOrder(context, baseSites)
        } else {
            baseSites
        }
        appStore.dispatch(AppAction.TopSitesChange(orderedSites))
    }
}
