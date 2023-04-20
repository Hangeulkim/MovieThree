package com.threemovie.threemovieapi.Service.impl

import com.threemovie.threemovieapi.Entity.DTO.Request.UpdateUserInfoRequest
import com.threemovie.threemovieapi.Entity.UserInfo
import com.threemovie.threemovieapi.Repository.Support.UserInfoRepositorySupport
import com.threemovie.threemovieapi.Repository.Support.UserLoginRepositorySupport
import com.threemovie.threemovieapi.Service.UserInfoService
import com.threemovie.threemovieapi.Utils.RandomKeyString
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserInfoServiceimpl(
	val userLoginRepositorySupport: UserLoginRepositorySupport,
	val userInfoRepositorySupport: UserInfoRepositorySupport,
	val emailService: EmailServiceimpl,
	val passwordEncoder: BCryptPasswordEncoder,
) : UserInfoService {
	
	fun updateUserInfo(userInfoRequest: UpdateUserInfoRequest): Boolean {
		userInfoRepositorySupport.updateUserInfo(userInfoRequest)
		
		return true
	}
	
	fun getUserInfo(email: String): UserInfo {
		return userInfoRepositorySupport.getUserInfo(email)
	}
	
	fun getNickName(email: String): String {
		return userInfoRepositorySupport.getNickName(email)
	}
	
	override fun resetPassword(email: String) {
		val pass = RandomKeyString.randomAlphabetNumber(20)
		val password = passwordEncoder.encode(pass)
		emailService.sendMessage(email, "resetpass", pass)
		
		userLoginRepositorySupport.updatePass(email, password)
	}
	
	override fun changePassword(email: String, pass: String) {
		val newPassword = passwordEncoder.encode(pass)
		emailService.sendMessage(email, "changepass", "changepass")
		
		userLoginRepositorySupport.updatePass(email, newPassword)
	}
	
	override fun existsEmail(email: String): Boolean {
		return userLoginRepositorySupport.existsEmail(email)
	}
	
	override fun existsNickName(nickName: String): Boolean {
		return userInfoRepositorySupport.existsNickName(nickName)
	}
}
