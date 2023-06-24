package com.threemovie.threemovieapi.global.config

import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import javax.sql.DataSource

@Configuration
class TestDBConfig {
	val driverClass = "org.h2.Driver"
	val url = "jdbc:h2:mem:testdb;MODE=mysql"
	val username = "mt"
	val pass = ""
	
	@Primary
	@Bean
	fun testDataSource(): DataSource =
		DataSourceBuilder.create()
			.driverClassName(driverClass)
			.url(url)
			.username(username)
			.password(pass)
			.build()
}
