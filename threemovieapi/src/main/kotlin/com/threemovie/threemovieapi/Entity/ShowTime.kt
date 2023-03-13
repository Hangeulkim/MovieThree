package com.threemovie.threemovieapi.Entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "ShowTimes")
data class ShowTime(
	@Column(name = "MovieId")
	val movieId: String = "",

	@Column(name = "MovieTheater")
	val movieTheater: String = "",

	@Column(name = "City")
	val city: String = "서울",

	@Column(name = "BrchKR")
	val brchKR: String = "",

	@Column(name = "BrchEN")
	val brchEN: String = "",

	@Column(name = "MovieKR")
	val movieKR: String = "",

	@Column(name = "MovieEN")
	val movieEN: String = "",

	@Column(name = "ScreenKR")
	val screenKR: String = "",

	@Column(name = "ScreenEN")
	val screemEN: String = "",

	@Column(name = "Date")
	val date: String = "",

	@Column(name = "StartTime")
	var startTime: String = "",

	@Column(name = "EndTime")
	var endTime: String = "",

	@Column(name = "RunningTime")
	var runningTime: Int = 200,

	@Column(name = "TotalSeat")
	val totalSeat: Int = 200,

	@Column(name = "RestSeat")
	var restSeat: Int = 200,

	@Column(name = "PlayKind")
	var playKind: String = "",

	@Id
	@Column(name = "TicketPage")
	val ticketPage: String = "",
)
