package org.ecodrizzle.de

object JsonRequestBodies {
    fun createDeviceRequestBody(deviceID: String, devEUI: String, joinEUI: String, applicationID: String, description: String): String {
        return """
            {
                "end_device": {
                    "name": "$deviceID",
                    "description": "$description",
                    "ids": {
                        "device_id": "$deviceID",
                        "dev_eui": "$devEUI",
                        "join_eui": "$joinEUI",
                        "application_ids": {
                            "application_id": "$applicationID"
                        }
                    },
                    "join_server_address": "zde.eu1.cloud.thethings.industries",
                    "network_server_address": "zde.eu1.cloud.thethings.industries",
                    "application_server_address": "zde.eu1.cloud.thethings.industries",
                    "version_ids": {
                        "brand_id": "heltec",
                        "model_id": "wireless-stick-class-a-otaa",
                        "hardware_version": "_unknown_hw_version_",
                        "firmware_version": "1.0",
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

    fun applicationServerRequestBody(deviceID: String, devEUI: String, joinEUI: String, applicationID: String): String {
        return """
            {
                "end_device": {
                    "ids": {
                        "device_id": "$deviceID",
                        "dev_eui": "$devEUI",
                        "join_eui": "$joinEUI",
                        "application_ids": {
                            "application_id": "$applicationID"
                        }
                    },
                    "version_ids": {
                        "brand_id": "heltec",
                        "model_id": "wireless-stick-class-a-otaa",
                        "hardware_version": "_unknown_hw_version_",
                        "firmware_version": "1.0",
                        "band_id": "EU_863_870"
                    },
                    "formatter": {
                        "up_formatter": "FORMATTER_APPLICATION"
                    }
                },
                "field_mask": {
                    "paths": [
                        "ids.device_id",
                        "ids.dev_eui",
                        "ids.join_eui",
                        "ids.application_ids.application_id",
                        "version_ids.brand_id",
                        "version_ids.model_id",
                        "version_ids.hardware_version",
                        "version_ids.firmware_version",
                        "version_ids.band_id",
                        "formatters.up_formatter",
                        "formatters.down_formatter"
                    ]
                }
            }
        """.trimIndent()
    }

    fun joinServerRequestBody(deviceID: String, appKey: String, devEUI: String, joinEUI: String, applicationID: String): String {
        return """
            {
                "end_device": {
                    "ids": {
                        "device_id": "$deviceID",
                        "dev_eui": "$devEUI",
                        "join_eui": "$joinEUI",
                        "application_ids": {
                            "application_id": "$applicationID"
                        }
                    },
                    "network_server_address": "zde.eu1.cloud.thethings.industries",
                    "application_server_address": "zde.eu1.cloud.thethings.industries",
                    "root_keys": {
                        "app_key": {
                            "key": "$appKey"
                        }
                    }
                },
                "field_mask": {
                    "paths": [
                        "network_server_address",
                        "application_server_address",
                        "root_keys.app_key.key",
                        "ids.device_id",
                        "ids.dev_eui",
                        "ids.join_eui",
                        "ids.application_ids.application_id"
                    ]
                }
            }
        """.trimIndent()
    }

    fun networkServerRequestBody(deviceID: String, devEUI: String, joinEUI: String, applicationID: String): String {
        return """
            {
                "end_device": {
                    "supports_join": true,
                    "lorawan_version": "MAC_V1_0_3",
                    "ids": {
                        "device_id": "$deviceID",
                        "dev_eui": "$devEUI",
                        "join_eui": "$joinEUI",
                        "application_ids": {
                            "application_id": "$applicationID"
                        }
                    },
                    "frequency_plan_id": "EU_863_870_TTN",
                    "version_ids": {
                        "brand_id": "heltec",
                        "model_id": "wireless-stick-class-a-otaa",
                        "hardware_version": "_unknown_hw_version_",
                        "firmware_version": "1.0",
                        "band_id": "EU_863_870"
                    },
                    "lorawan_phy_version": "PHY_V1_0_3_REV_A",
                    "mac_settings": {
                        "class_c_timeout": "60s",
                        "supports_32_bit_f_cnt": true
                    }
                },
                "field_mask": {
                    "paths": [
                        "supports_join",
                        "lorawan_version",
                        "ids.device_id",
                        "ids.dev_eui",
                        "ids.join_eui",
                        "ids.application_ids.application_id",
                        "frequency_plan_id",
                        "version_ids.brand_id",
                        "version_ids.model_id",
                        "version_ids.hardware_version",
                        "version_ids.firmware_version",
                        "version_ids.band_id",
                        "lorawan_phy_version",
                        "mac_settings.class_c_timeout",
                        "mac_settings.supports_32_bit_f_cnt"
                    ]
                }
            }
        """.trimIndent()
    }
}