package br.com.zup.edu.models.enums

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

internal class KeyTypeEnumTest{

    @Nested
    inner class RANDOM{

        @Test
        fun `deve retornar true quando a chave nao for passada`(){
            with(KeyTypeEnum.RANDOM){
                assertTrue(isValid(null))
            }
        }

        @Test
        fun `deve retornar false quando a chave for passada`(){
            with(KeyTypeEnum.RANDOM){
                assertFalse(isValid(UUID.randomUUID().toString()))
            }
        }
    }

    @Nested
    inner class PHONE{

        @Test
        fun `deve retornar false quando a chave passada for nula`(){
            with(KeyTypeEnum.PHONE){
                assertFalse(isValid(null))
            }
        }

        @Test
        fun `deve retornar true quando a chave passada for no formato valido`(){
            with(KeyTypeEnum.PHONE){
                assertTrue(isValid("+1212121212"))
            }
        }

        @Test
        fun `deve retornar false quando a chave passada for no formato invalido`(){
            with(KeyTypeEnum.PHONE){
                assertFalse(isValid("12323"))
            }
        }
    }

    @Nested
    inner class EMAIL{

        @Test
        fun `deve retornar true quando a chave passada for no formato valido`(){
            with(KeyTypeEnum.EMAIL){
                assertTrue(isValid("lucas@email.com"))
            }
        }

        @Test
        fun `deve retornar false quando a chave passada for nula`(){
            with(KeyTypeEnum.EMAIL){
                assertFalse(isValid(null))
            }
        }

        @Test
        fun `deve retornar false quando a chave passada for no formato invalido`(){
            with(KeyTypeEnum.EMAIL){
                assertFalse(isValid("lucaswilliam"))
            }
        }
    }

    @Nested
    inner class CPF{

        @Test
        fun `deve retornar true quando a chave passada for um cpf valido`(){
            with(KeyTypeEnum.CPF){
                assertTrue(isValid("23998987029"))
            }
        }

        @Test
        fun `deve retornar false quando a chave passada for um cpf invalido`(){
            with(KeyTypeEnum.CPF){
                assertFalse(isValid("12121212121"))
            }
        }

        @Test
        fun `deve retornar false quando a chave passada for nula`(){
            with(KeyTypeEnum.CPF){
                assertFalse(isValid(null))
            }
        }

        @Test
        fun `deve retornar false quando a chave passada for de um tamanho invalido`(){
            with(KeyTypeEnum.CPF){
                assertFalse(isValid("111"))
            }
        }
    }

}