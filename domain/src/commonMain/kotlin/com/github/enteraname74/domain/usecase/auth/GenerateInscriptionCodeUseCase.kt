package com.github.enteraname74.domain.usecase.auth

import com.github.enteraname74.domain.model.CloudInscriptionCode
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.repository.AuthRepository

class GenerateInscriptionCodeUseCase(
    private val authRepo: AuthRepository
) {
    suspend operator fun invoke(): SoulResult<CloudInscriptionCode> =
        authRepo.generateInscriptionCode()
}