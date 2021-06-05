package br.com.zup.edu.utils

import br.com.zup.edu.DeletePixKeyRequest
import br.com.zup.edu.RegisterPixKeyRequest
import br.com.zup.edu.RegisterPixKeyRequestOrBuilder
import br.com.zup.edu.dto.DeleteKeyRequest
import br.com.zup.edu.dto.KeyRequest
import br.com.zup.edu.models.enums.AccountTypeEnum
import br.com.zup.edu.models.enums.KeyTypeEnum

fun RegisterPixKeyRequest.toModel(): KeyRequest{
    return KeyRequest(keyValue = this.keyValue,
                    keyType = KeyTypeEnum.valueOf(this.keyType!!.name),
                    account = AccountTypeEnum.valueOf(this.account!!.name),
                    idClient = this.idClient)
}

fun DeletePixKeyRequest.toModel(): DeleteKeyRequest{
    return DeleteKeyRequest(this.clientId, this.pixId)
}
