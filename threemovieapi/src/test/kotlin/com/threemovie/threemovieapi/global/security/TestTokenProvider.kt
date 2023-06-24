package com.threemovie.threemovieapi.global.security

import com.threemovie.threemovieapi.global.security.response.TokenResponse
import com.threemovie.threemovieapi.global.security.service.JwtTokenProvider
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Primary
@Component
class TestTokenProvide : JwtTokenProvider("asdfksdkfsdkasdlksadlkjfasdjklfskaldjflakjsd", 360000, 360000) {
	
	override fun createAllToken(email: String, userRole: String, nickName: String) =
		TokenResponse(
			generateAccessToken(email, userRole),
			generateRefreshToken(email),
			nickName
		)
	
	override fun generateRefreshToken(email: String): String {
		return "test-token"
	}
	
	override fun generateAccessToken(email: String, userRole: String): String {
		return "test-token"
	}
	
	override fun getEmail(token: String): String {
		return "test@movie.com"
	}
	
	override fun validateToken(token: String) =
		token == "test-token"
}
