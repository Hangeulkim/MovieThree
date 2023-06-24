package com.threemovie.threemovieapi.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class TestPassEncoderConfig {
	
	@Primary
	@Bean
	fun testPassEncoder() =
		object : PasswordEncoder {
			override fun encode(rawPassword: CharSequence?) =
				rawPassword.toString()
			
			override fun matches(rawPassword: CharSequence?, encodedPassword: String?) =
				rawPassword == encodedPassword
			
			override fun upgradeEncoding(encodedPassword: String?) = false
		}
}
