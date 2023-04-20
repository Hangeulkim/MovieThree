package com.threemovie.threemovieapi.Service

interface UserInfoService {
	fun resetPassword(email: String)
	
	fun changePassword(email: String, pass: String)
	
	fun changeNickName(email: String, nickName: String)
	
	fun existsEmail(email: String): Boolean
	
	fun existsNickName(nickName: String): Boolean
	
}