package com.threemovie.threemovieapi.domain.user.controller

import com.threemovie.threemovieapi.domain.user.controller.request.*
import com.threemovie.threemovieapi.domain.user.controller.response.AccessTokenResponse
import com.threemovie.threemovieapi.domain.user.service.UserAuthService
import com.threemovie.threemovieapi.domain.user.service.UserDataService
import com.threemovie.threemovieapi.global.security.config.UserRole
import com.threemovie.threemovieapi.global.security.response.TokenResponse
import com.threemovie.threemovieapi.global.security.service.JwtTokenProvider
import com.threemovie.threemovieapi.global.security.service.RedisUtil
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@Tag(name = "UserAuthController", description = "인증 관련 컨트롤러")
@RestController
@CrossOrigin(origins = ["http://localhost:3000"])
@RequestMapping("/api/auth")
class UserAuthController(
	val userAuthService: UserAuthService,
	val userDataService: UserDataService,
	val jwtTokenProvider: JwtTokenProvider,
	val redisUtil: RedisUtil,
) {
	@PostMapping("/send/code")
	fun sendAuthEmail(@RequestBody emailRequest: EmailRequest): ResponseEntity<String> {
		val (email, isSignUp) = emailRequest
		userDataService.existsEmail(email, isSignUp)
		
		userAuthService.sendAuth(email)
		return ResponseEntity.status(HttpStatus.OK).body("success")
	}
	
	@PostMapping("/check/code")
	fun checkAuthCode(@RequestBody authRequest: AuthRequest): ResponseEntity<String> {
		val (email, authCode) = authRequest
		userAuthService.checkAuth(email, authCode)
		
		return ResponseEntity.status(HttpStatus.OK).body("성공적으로 인증 되었습니다.")
	}
	
	@PostMapping("/login")
	fun loginAccount(@RequestBody loginRequest: LoginRequest): ResponseEntity<TokenResponse> {
		val (email, pass) = loginRequest
		
		userAuthService.loginAccount(email, pass)
		
		val nickName = userDataService.getNickName(email)
		val userRole = UserRole.USER.toString()
		val retToken = jwtTokenProvider.createAllToken(email, userRole, nickName)
		
		redisUtil.setDataExpire(email, retToken.refreshToken, jwtTokenProvider.REFRESH_TOKEN_EXPIRE_TIME.toLong())
		
		return ResponseEntity.status(HttpStatus.OK)
			.body(retToken)
	}
	
	@PostMapping("/logout")
	fun logoutAccount(request: HttpServletRequest): ResponseEntity<String> {
		val accessToken = request.getHeader("Authorization").substring(7)
		
		userAuthService.logoutAccount(accessToken)
		
		return ResponseEntity.status(HttpStatus.OK).body("성공적으로 로그아웃 됐습니다.")
	}
	
	@PostMapping("/signup")
	fun signUpAccount(@RequestBody account: AccountSignUpRequest): ResponseEntity<String> {
		userAuthService.existsAuth(account.email)
		
		userDataService.existsEmail(account.email, true)
		userDataService.existsNickName(account.nickName)
		
		val (email, pass, nickName, sex, birth) = account
		userAuthService.signUpAccount(email, pass, nickName, sex, birth)
		return ResponseEntity.status(HttpStatus.OK).body("성공적으로 가입 되었습니다.")
	}
	
	@DeleteMapping("/signout")
	fun signOutAccount(request: HttpServletRequest): ResponseEntity<String> {
		val accessToken = request.getHeader("Authorization").substring(7)
		userAuthService.signOutAccount(accessToken)
		return ResponseEntity.status(HttpStatus.OK).body("성공적으로 탈퇴 되었습니다.")
	}
	
	@PostMapping("/reissue")
	fun reissue(request: HttpServletRequest): ResponseEntity<AccessTokenResponse> {
		val refreshToken = request.getHeader("Authorization").substring(7)
		
		userAuthService.reissue(refreshToken)
		
		val userRole = UserRole.USER.toString()
		val email = jwtTokenProvider.getEmail(refreshToken)
		val accessToken = jwtTokenProvider.generateAccessToken(email, userRole)
		val ret = AccessTokenResponse(accessToken)
		
		return ResponseEntity.status(HttpStatus.OK)
			.body(ret)
	}
	
	@PostMapping("/password")
	fun changePassword(@RequestBody loginRequest: LoginRequest): ResponseEntity<String> {
		val (email, pass) = loginRequest
		userDataService.changePassword(email, pass)
		return ResponseEntity.status(HttpStatus.OK).body("비밀번호가 변경 되었습니다. 다시 로그인 해주시기 바랍니다.")
	}
}
