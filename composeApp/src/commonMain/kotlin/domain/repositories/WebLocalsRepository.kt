package domain.repositories

import domain.model.SiteEntity

interface WebLocalsRepository {
    suspend fun loadAllLocals(): List<SiteEntity>
    suspend fun modifyWebLocal(siteEntity: SiteEntity): SiteEntity
    suspend fun deleteWebLocal(localId: String)
    suspend fun loadWebLocal(localId: String): SiteEntity?
}