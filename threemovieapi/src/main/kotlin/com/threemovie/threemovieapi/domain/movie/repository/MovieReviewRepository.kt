package com.threemovie.threemovieapi.domain.movie.repository

import com.threemovie.threemovieapi.domain.movie.entity.domain.MovieReview
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.*

interface MovieReviewRepository : JpaRepository<MovieReview, UUID> {
	@Transactional
	@Modifying
	@Query(value = "truncate movie_review", nativeQuery = true)
	fun truncate()
}
