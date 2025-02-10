package org.ecodrizzle.de

object JsonRequestBodies {
    fun createDeviceRequestBody(deviceID: String, devEUI: String, joinEUI: String, applicationID: String): String{
        return """
            {
        "end_device": {
            "name": "bbb_device_name",
            "description": "device-description",
            "ids": {
                "device_id": "$deviceID",
                "dev_eui": "$devEUI",
                "join_eui": "$joinEUI",
                "application_ids": {
                    "application_id": "$applicationID"
                }
            },
            "join_server_address": "eu1.cloud.thethings.network",
            "network_server_address": "eu1.cloud.thethings.network",
            "application_server_address": "eu1.cloud.thethings.network",
            "version_ids": {
                "brand_id": "dragino",
                "model_id": "lht65",
                "hardware_version": "_unknown_hw_version_",
                "firmware_version": "1.8",
                "band_id": "EU_863_870"
            }
        },
        "field_mask": {
            "paths": [
                "join_server_address",
                "network_server_address",
                "application_server_address",
                "version_ids.brand_id",
                "version_ids.model_id",
                "version_ids.hardware_version",
                "version_ids.firmware_version",
                "version_ids.band_id"
            ]
        }
    }
        """.trimIndent()
    }
}