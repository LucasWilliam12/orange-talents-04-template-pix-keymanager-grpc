package br.com.zup.edu.repositories

import br.com.zup.edu.models.KeyModel
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface KeyRepository: JpaRepository<KeyModel, UUID> {
    fun existsByKeyValue(value: String): Boolean
    fun findByPixIdAndIdClient(pixId: UUID, idClient: UUID): Optional<KeyModel>
    fun deleteByPixId(pixId: UUID)
}