package com.threemovie.threemovieapi.Repository

import com.threemovie.threemovieapi.Entity.ShowTime
import org.springframework.data.jpa.repository.JpaRepository

interface ShowTimeRepository : JpaRepository<ShowTime, Long>