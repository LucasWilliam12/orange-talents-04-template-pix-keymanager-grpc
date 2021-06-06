package br.com.zup.edu.utils

import br.com.zup.edu.clients.AccountResponse
import br.com.zup.edu.clients.BasicAccountResponse
import br.com.zup.edu.clients.InstituicaoResponse
import br.com.zup.edu.clients.TitularResponse
import br.com.zup.edu.endpoints.RegisterKeyPixEndpointTest
import br.com.zup.edu.models.enums.AccountTypeEnum

class Util {
    companion object{
        private val instituicaoResponse = InstituicaoResponse("Itau", "21212342")
        fun createAccountResponse(): AccountResponse {
            val titularResponse = TitularResponse(id = RegisterKeyPixEndpointTest.client_id.toString(), nome = "Usuario teste", cpf = "12345678911")
            return AccountResponse(tipo = AccountTypeEnum.CONTA_CORRENTE.name,
                instituicao = instituicaoResponse, agencia = "1234",
                numero = "3323232", titular = titularResponse)
        }

        fun createAccountBasicResponse(): BasicAccountResponse{
            return BasicAccountResponse(
                RegisterKeyPixEndpointTest.client_id.toString(),
                nome = "Usuario teste",
                cpf = "12345678911",
                instituicaoResponse
            )
        }
    }
}