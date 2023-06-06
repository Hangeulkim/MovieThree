package com.threemovie.threemovieapi.domain.showtime.entity.domain

import com.threemovie.threemovieapi.domain.movie.entity.domain.MovieData
import com.threemovie.threemovieapi.domain.theater.entity.domain.TheaterData
import com.threemovie.threemovieapi.global.entity.PrimaryKeyEntity
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.SQLInsert

@Entity
@Table(
	name = "ShowTime",
	uniqueConstraints = [UniqueConstraint(
		name = "show_time_value_uk",
		columnNames = ["movie_id", "theater_data_id", "screenKr", "showYmd", "playKind"]
	)]
)
@SQLInsert(
	sql = "INSERT IGNORE INTO show_time(movie_id, play_kind, screen_en, screen_kr, show_ymd, theater_data_id, total_seat, id)" +
			"VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
)
class ShowTime(
	@NotNull
	@Column(length = 20)
	val screenKr: String = "",
	
	@NotNull
	val screenEn: String = "",
	
	@NotNull
	val showYmd: Int = 0,
	
	@NotNull
	val totalSeat: Int = 200,
	
	@NotNull
	val playKind: String = "",
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "theater_data_id")
	val theaterData: TheaterData
) : PrimaryKeyEntity() {
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "movie_id",
		referencedColumnName = "movieId",
		foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT)
	)
	var movieData: MovieData? = null
	
	@OneToMany(
		mappedBy = "showTime",
		orphanRemoval = true,
		cascade = [CascadeType.ALL],
		fetch = FetchType.LAZY
	)
	var showTimeReserve: MutableList<ShowTimeReserve> = ArrayList()
		protected set
	
	fun addReservation(showTimeReserve: List<ShowTimeReserve>) {
		this.showTimeReserve.addAll(showTimeReserve)
	}
}
