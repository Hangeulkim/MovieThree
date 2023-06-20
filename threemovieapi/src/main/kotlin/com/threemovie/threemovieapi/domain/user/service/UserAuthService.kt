package com.threemovie.threemovieapi.domain.user.service

import com.threemovie.threemovieapi.domain.user.entity.domain.UserData
import com.threemovie.threemovieapi.domain.user.entity.domain.UserLogin
import com.threemovie.threemovieapi.domain.user.entity.domain.UserSignUpAuth
import com.threemovie.threemovieapi.domain.user.exception.AccountNotFoundException
import com.threemovie.threemovieapi.domain.user.exception.AccountPasswordMissMatchException
import com.threemovie.threemovieapi.domain.user.exception.AuthCodeMissMatchException
import com.threemovie.threemovieapi.domain.user.exception.AuthNotFoundException
import com.threemovie.threemovieapi.domain.user.repository.UserLoginRepository
import com.threemovie.threemovieapi.domain.user.repository.UserSignUpAuthRepository
import com.threemovie.threemovieapi.domain.user.repository.support.UserDataRepositorySupport
import com.threemovie.threemovieapi.domain.user.repository.support.UserLoginRepositorySupport
import com.threemovie.threemovieapi.domain.user.repository.support.UserSignUpRepositorySupport
import com.threemovie.threemovieapi.domain.user.service.impl.AuthEmailServiceImpl
import com.threemovie.threemovieapi.global.security.exception.IllegalTokenException
import com.threemovie.threemovieapi.global.security.service.JwtTokenProvider
import com.threemovie.threemovieapi.global.security.service.RedisUtil
import com.threemovie.threemovieapi.global.service.RandomKeyString
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class UserAuthService(
	val userSignUpRepositorySupport: UserSignUpRepositorySupport,
	val userLoginRepositorySupport: UserLoginRepositorySupport,
	val userLoginRepository: UserLoginRepository,
	val userdataRepositorySupport: UserDataRepositorySupport,
	val authEmailServiceImpl: AuthEmailServiceImpl,
	val jwtTokenProvider: JwtTokenProvider,
	val passwordEncoder: BCryptPasswordEncoder,
	val redisUtil: RedisUtil,
	val randomKeyString: RandomKeyString,
	val userSignUpAuthRepository: UserSignUpAuthRepository
) {
	fun sendAuth(email: String) {
		userSignUpRepositorySupport.deletePrevAuth(email)
		val pass = randomKeyString.randomAlphabetNumber(8)
		authEmailServiceImpl.sendMessage(email, pass)
		
		val date = LocalDateTime.now().plusMinutes(5)
		val userSignUpAuth = UserSignUpAuth(email, pass, date, false)
		userSignUpAuthRepository.save(userSignUpAuth)
	}
	
	fun signUpAccount(email: String, pass: String, nickName: String, sex: Boolean?, birth: LocalDate?) {
		val encodePass = passwordEncoder.encode(pass)
		
		val userLogin = UserLogin(email, encodePass)
		val userdata = UserData(nickName, sex, birth?.plusDays(1))
		userLogin.userData = userdata
		userdata.userLogin = userLogin
		userLoginRepository.save(userLogin)
		
		deletePrevAuth(email)
	}
	
	fun signOutAccount(accessToken: String) {
		val email = jwtTokenProvider.getEmail(accessToken)
		
		val id = userLoginRepositorySupport.getUserDataIdByEmail(email)
		userdataRepositorySupport.deleteUserData(id)
		userLoginRepositorySupport.deleteUserLogin(email)
		redisUtil.deleteData(email)
		
	}
	
	fun loginAccount(email: String, pass: String): Boolean {
		val userLogin = userLoginRepositorySupport.getUserLoginByEmail(email) ?: throw AccountNotFoundException()
		if (! passwordEncoder.matches(
				pass,
				userLogin.password
			)
		)
			throw AccountPasswordMissMatchException()
		
		return true
	}
	
	fun logoutAccount(accessToken: String) {
		if (! jwtTokenProvider.validateToken(accessToken))
			throw IllegalTokenException()
		
		val email = jwtTokenProvider.getEmail(accessToken)
		if (! redisUtil.getData(email).isNullOrEmpty())
			redisUtil.deleteData(email)
		
		val expiration = jwtTokenProvider.getExpiration(accessToken)
		redisUtil.setDataExpire(accessToken, "logout", expiration)
	}
	
	fun reissue(refreshToken: String): Boolean {
		if (refreshToken.isNullOrEmpty() || ! jwtTokenProvider.validateToken(refreshToken))
			throw IllegalTokenException()
		return true
	}
	
	fun existsAuth(email: String): Boolean {
		if (! userSignUpRepositorySupport.existsSuccessCode(email))
			throw AuthNotFoundException()
		return true
	}
	
	fun checkAuth(email: String, authCode: String) {
		val ret = userSignUpRepositorySupport.checkAuthCode(email, authCode)
		if (ret)
			updateAuth(email)
		else
			throw AuthCodeMissMatchException()
	}
	
	fun updateAuth(email: String) {
		userSignUpRepositorySupport.updateAuthSuccess(email)
	}
	
	fun deletePrevAuth(email: String) {
		userSignUpRepositorySupport.deletePrevAuth(email)
	}
}
