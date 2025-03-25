package com.github.enteraname74.domain.model.settings

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


internal data class SettingFlowInformation<DataType>(
    val key: String,
    val retrieveValue: (key:String) -> DataType,
) {
    val flow: MutableStateFlow<DataType> = MutableStateFlow(retrieveValue(key))

    fun update() {
        flow.value = retrieveValue(key)
    }
}

internal object SettingsFlowSystem {
    private val allFlows: ArrayList<SettingFlowInformation<*>> = arrayListOf()
    private val mutex: Mutex = Mutex()

    /**
     * Adds a flow to the list of flows of the system if it doesn't exist.
     * Returns the added or found flow.
     */
    @Suppress("UNCHECKED_CAST")
    suspend fun <DataType> addFlowIfNotExist(settingFlowInformation: SettingFlowInformation<DataType>): SettingFlowInformation<DataType> {
        mutex.withLock {
            val foundElement: SettingFlowInformation<*>? = allFlows.firstOrNull { it.key == settingFlowInformation.key }
            return if (foundElement == null) {

                allFlows.add(settingFlowInformation)
                settingFlowInformation
            } else {
                foundElement as SettingFlowInformation<DataType>
            }
        }
    }

    /**
     * Updates the flow that is linked to the given key.
     */
    @Suppress("UNCHECKED_CAST")
    fun <DataType> update(key: String) {
        try {
            val foundElement: SettingFlowInformation<DataType>? =
                allFlows.firstOrNull { it.key == key } as? SettingFlowInformation<DataType>

            foundElement?.update()
        } catch (_: Exception) { }
    }
}

