package com.dev.fabricio.gonzalez.mediaapp

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import data.datastore.DATA_STORE_FILE_NAME

fun createDataStore(context: Context): DataStore<Preferences> {

    return data.datastore.createDataStore {
        context.filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath
    }
}