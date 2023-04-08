package com.aws.amazonlocation.domain.repository

import com.amazonaws.services.geo.model.Step
import com.aws.amazonlocation.domain.`interface`.DistanceInterface
import com.aws.amazonlocation.domain.`interface`.NavigationDataInterface
import com.aws.amazonlocation.domain.`interface`.SearchDataInterface
import com.aws.amazonlocation.domain.`interface`.SearchPlaceInterface

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

// SPDX-License-Identifier: MIT-0
interface LocationSearchRepository {

    fun searchPlaceSuggestions(
        lat: Double?,
        lng: Double?,
        searchText: String,
        searchPlace: SearchPlaceInterface
    )

    fun searchPlaceIndexForText(
        lat: Double?,
        lng: Double?,
        searchText: String?,
        searchPlace: SearchPlaceInterface
    )

    fun calculateDistance(
        latDeparture: Double?,
        lngDeparture: Double?,
        latDestination: Double?,
        lngDestination: Double?,
        isAvoidFerries: Boolean?,
        isAvoidTolls: Boolean?,
        travelMode: String?,
        distanceInterface: DistanceInterface
    )

    fun searchNavigationPlaceIndexForPosition(
        lat: Double?,
        lng: Double?,
        step: Step,
        searchPlace: NavigationDataInterface
    )

    fun searPlaceIndexForPosition(
        lat: Double?,
        lng: Double?,
        searchPlace: SearchDataInterface
    )
}
