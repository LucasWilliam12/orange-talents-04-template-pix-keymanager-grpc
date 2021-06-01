package br.com.zup.edu.services

import br.com.zup.edu.clients.AccountResponse
import br.com.zup.edu.clients.ItauClient
import br.com.zup.edu.dto.KeyRequest
import br.com.zup.edu.exceptions.PixKeyAlreadyExistsException
import br.com.zup.edu.models.KeyModel
import br.com.zup.edu.repositories.KeyRepository
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class RegisterNewKeyPixService (@Inject val repository: KeyRepository, @Inject val itauClient: ItauClient) {

    @Transactional
    fun register(@Valid request: KeyRequest): KeyModel{

        val logger = LoggerFactory.getLogger(RegisterNewKeyPixService::class.java)

        // Verificando se o cliente realmente existe
        var account: AccountResponse? = null
        try {
            val response = itauClient.findUserByAccountTypeAndId(tipo = request.account!!.name, id = request.idClient!!)
            account = response.body()
        }catch(e: Exception){
            throw IllegalArgumentException("Cliente informado não foi encontrado")
        }

        val newKey = request.toModel(account!!.toModel())

        // Verificando se já tem uma chave com o mesmo valor
        if(repository.existsByKeyValue(newKey.keyValue)){
            logger.error("chave já cadasrada")
            throw PixKeyAlreadyExistsException("A chave pix informada já existe")
        }

        // Tentando salvar o objeto
        repository.save(newKey)

        return newKey
    }


}