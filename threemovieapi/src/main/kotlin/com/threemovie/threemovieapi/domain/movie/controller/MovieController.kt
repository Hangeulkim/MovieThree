package com.threemovie.threemovieapi.domain.movie.controller

import com.threemovie.threemovieapi.domain.movie.entity.dto.MovieDetailDTO
import com.threemovie.threemovieapi.domain.movie.entity.dto.MovieMainDTO
import com.threemovie.threemovieapi.domain.movie.entity.dto.MovieSearchDTO
import com.threemovie.threemovieapi.domain.movie.service.MovieDataControlService
import com.threemovie.threemovieapi.domain.movie.service.MovieDataService
import com.threemovie.threemovieapi.domain.movie.service.MovieSearchService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@Tag(name = "movie", description = "영화 데이터 관련 컨트롤러")
@RestController
@RequestMapping("/api/movie")
class MovieController(
	val movieService: MovieDataService,
	val movieDataControlService: MovieDataControlService,
	val movieSearchService: MovieSearchService
) {
	@GetMapping("/main")
	fun getMovieList(): ResponseEntity<List<MovieMainDTO>> {
		val ret = movieService.getMovieList()
		return ResponseEntity.ok(ret)
	}
	@GetMapping("/detail/{movieId}")
	fun getMovieDetail(@PathVariable movieId: String): ResponseEntity<MovieDetailDTO> {
		return ResponseEntity.ok(movieService.getMovieDetail(movieId))
	}

	@GetMapping("/search/{keyword}", "/search/")
	fun getMovieSearchKeyword(@PathVariable(required = false) keyword: String?): ResponseEntity<Set<MovieSearchDTO>> {
		val ret = movieService.getMovieByKeyword(keyword)
		println(ret)
		return ResponseEntity.ok(ret)
	}
}
