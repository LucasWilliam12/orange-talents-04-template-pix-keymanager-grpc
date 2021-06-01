package br.com.zup.edu.utils

import br.com.zup.edu.RegisterPixKeyRequestOrBuilder
import br.com.zup.edu.dto.KeyRequest
import br.com.zup.edu.models.enums.KeyTypeEnum

fun RegisterPixKeyRequestOrBuilder.toModel(): KeyRequest{
    return KeyRequest(keyValue = this.keyValue,
                    keyType = KeyTypeEnum.valueOf(this.keyType!!.name),
                    account = this.account,
                    idClient = this.idClient)
}