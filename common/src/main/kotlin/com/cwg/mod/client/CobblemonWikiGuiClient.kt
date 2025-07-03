/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cwg.mod.client

import com.cobblemon.mod.common.Cobblemon.LOGGER
import com.cwg.mod.CobblemonWikiGuiClientImplementation

object CobblemonWikiGuiClient {

    lateinit var implementation: CobblemonWikiGuiClientImplementation

    fun initialize(implementation: CobblemonWikiGuiClientImplementation) {
        LOGGER.info("Initializing CobblemonWikiGui client")
        this.implementation = implementation
    }
}