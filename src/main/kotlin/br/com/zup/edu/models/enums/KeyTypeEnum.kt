package br.com.zup.edu.models.enums

import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator

enum class KeyTypeEnum {
    EMAIL {
        override fun isValid(key: String?): Boolean{
            if(key.isNullOrBlank()){
                return false
            }

            return EmailValidator().run {
                initialize(null)
                isValid(key, null)
            }
        }
    },
    PHONE {
        override fun isValid(key: String?): Boolean {
            if(key.isNullOrBlank()){
                return false
            }
            return key.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
        }
    },
    CPF {
        override fun isValid(key: String?): Boolean{
            if(key.isNullOrBlank()){
                return false
            }

            if(!key.matches("^[0-9]{11}\$".toRegex())){
                return false
            }

            return CPFValidator().run {
                initialize(null)
                isValid(key, null)
            }
        }
    },

    RANDOM {
        override fun isValid(key: String?): Boolean {
            return key.isNullOrBlank()
        }
    };

    abstract fun isValid(key: String?): Boolean

}