package com.threemovie.threemovieapi.domain.showtime.scheduler

import com.threemovie.threemovieapi.domain.movie.entity.domain.MovieData
import com.threemovie.threemovieapi.domain.movie.entity.dto.MovieNameDTO
import com.threemovie.threemovieapi.domain.movie.entity.dto.MovieNameInfoVO
import com.threemovie.threemovieapi.domain.movie.repository.support.MovieDataRepositorySupport
import com.threemovie.threemovieapi.domain.movie.service.MovieSearchService
import com.threemovie.threemovieapi.domain.showtime.entity.domain.ShowTime
import com.threemovie.threemovieapi.domain.showtime.entity.domain.ShowTimeReserve
import com.threemovie.threemovieapi.domain.showtime.entity.dto.ShowTimeVO
import com.threemovie.threemovieapi.domain.showtime.repository.ShowTimeRepository
import com.threemovie.threemovieapi.domain.showtime.repository.support.ShowTimeRepositorySupport
import com.threemovie.threemovieapi.domain.showtime.repository.support.ShowTimeReserveRepositorySupport
import com.threemovie.threemovieapi.domain.theater.entity.domain.TheaterData
import com.threemovie.threemovieapi.domain.theater.repository.support.TheaterDataRepositorySupport
import com.threemovie.threemovieapi.global.entity.LastUpdateTime
import com.threemovie.threemovieapi.global.repository.LastUpdateTimeRepository
import com.threemovie.threemovieapi.global.repository.support.LastUpdateTimeRepositorySupport
import com.threemovie.threemovieapi.global.service.CalcSimilarity
import com.threemovie.threemovieapi.global.service.ChkNeedUpdate
import kotlinx.coroutines.*
import org.json.JSONObject
import org.jsoup.Jsoup
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.regex.Matcher
import java.util.regex.Pattern

@Component
class ShowTimeScheduler(
	val lastUpdateTimeRepositorySupport: LastUpdateTimeRepositorySupport,
	val theaterDataRepositorySupport: TheaterDataRepositorySupport,
	val movieDataRepositorySupport: MovieDataRepositorySupport,
	val lastUpdateTimeRepository: LastUpdateTimeRepository,
	val showTimeRepository: ShowTimeRepository,
	val showTimeReserveRepositorySupport: ShowTimeReserveRepositorySupport,
	val showTimeRepositorySupport: ShowTimeRepositorySupport,
	val movieSearchService: MovieSearchService
) {
	val userAgent: String =
		"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36"
	val CGVurl = "http://www.cgv.co.kr"
	val LCurl = "http://www.lottecinema.co.kr"
	val mburl = "https://www.megabox.co.kr"
	val nameMap = HashMap<String, MovieNameInfoVO>()
	lateinit var movieNameInfo: List<MovieNameDTO>
	val square = "\\[[^)]*\\]".toRegex()
	val code = "showtime"
	var time = 202302110107
	
	@Async
	@Scheduled(cron = "0 0/5 * * * ?")
	fun chkMovieShowingTime() {
		var chkTime = lastUpdateTimeRepositorySupport.getLastTime(code)
		if (chkTime == null) {
			lastUpdateTimeRepository.save(LastUpdateTime(code, 202302110107))
			chkTime = 202302110107
		}
		if (ChkNeedUpdate.chkUpdateFiveMinute(chkTime)) {
			time = ChkNeedUpdate.retFormatterTime()
			lastUpdateTimeRepositorySupport.updateLastTime(time, code)
			movieNameInfo = movieDataRepositorySupport.getMovieNameData()
			
			val mbTheaters = theaterDataRepositorySupport.getTheaterEntityByMT("MB")
			val lcTheaters = theaterDataRepositorySupport.getTheaterEntityByMT("LC")
			val cgvTheaters = theaterDataRepositorySupport.getTheaterEntityByMT("CGV")
			
			val showTimeList: MutableList<ShowTime> = ArrayList()
			val showTimeAsync: MutableList<Deferred<List<ShowTime>>> = ArrayList()
			
			
			runBlocking {
				for (mbTheater in mbTheaters) {
					CoroutineScope(Dispatchers.IO).async { updateMBShowtimes(mbTheater) }
						.also { showTimeAsync.add(it) }
				}
				for (lcTheater in lcTheaters) {
					CoroutineScope(Dispatchers.IO).async { updateLCShowtimes(lcTheater) }
						.also { showTimeAsync.add(it) }
				}
				for (cgvTheater in cgvTheaters) {
					CoroutineScope(Dispatchers.IO).async { updateCGVShowtimes(cgvTheater) }
						.also { showTimeAsync.add(it) }
				}
				for (showTimeDeferred in showTimeAsync) {
					showTimeList.addAll(showTimeDeferred.await())
				}
			}
			
			showTimeRepository.saveAll(showTimeList)
			showTimeReserveRepositorySupport.deleteShowTimeReserveByTime(time)
			showTimeRepositorySupport.deleteZeroReserveShowTime(time)
		}
	}
	
	fun getMBDates(brchNo: String, brchName: String): ArrayList<String> {
		val url = mburl + "/on/oh/ohc/Brch/schedulePage.do"
		
		val paramlist = HashMap<String, String>()
		paramlist["brchNm"] = brchName
		paramlist["brchNo"] = brchNo
		paramlist["brchNo1"] = brchNo
		paramlist["firstAt"] = "Y"
		paramlist["masterType"] = "brch"
		val conn = Jsoup.connect(url)
			.userAgent(userAgent)
			.data(paramlist)
			.ignoreContentType(true)//에러나면 추가
		val doc = conn.post().text().replace("\"\"", "\"")
		
		val datemap = ArrayList<String>()
		val megamap = JSONObject(doc).getJSONObject("megaMap")
		val showtimes = megamap.getJSONArray("movieFormDeList")
		
		
		for (i in 0 until showtimes.length()) {
			val showtime = showtimes.getJSONObject(i)
			val date = showtime.getString("playDe")
			
			datemap.add(date)
		}
		
		return datemap
	}
	
	fun updateMBShowtimes(
		theaterData: TheaterData
	): List<ShowTime> {
		
		var showTimeList = ArrayList<ShowTime>()
		val url = mburl + "/on/oh/ohc/Brch/schedulePage.do"
		val brchKR = theaterData.brchKr
		
		val brchNo = theaterData.theaterCode
		
		val dates = getMBDates(brchNo, brchKR)
		
		for (date in dates) {
			val paramlist = HashMap<String, String>()
			paramlist["brchNm"] = brchKR
			paramlist["brchNo"] = brchNo
			paramlist["brchNo1"] = brchNo
			paramlist["masterType"] = "brch"
			paramlist["playDe"] = date
			paramlist["firstAt"] = "N"
			val conn = Jsoup.connect(url)
				.userAgent(userAgent)
				.data(paramlist)
				.ignoreContentType(true)//에러나면 추가
			val doc = conn.post().body().text()
			val showtimes = JSONObject(doc).getJSONObject("megaMap").getJSONArray("movieFormList")
			
			val dtos = HashMap<Triple<String, String, String>, ShowTimeVO>()
			for (i in 0 until showtimes.length()) {
				val showtime = showtimes.getJSONObject(i)
				val totalSeat = showtime.getInt("totSeatCnt")
				val playSchldNo = showtime.getString("playSchdlNo")
				val movieKR = showtime.getString("rpstMovieNm")
				val playKind = showtime.getString("playKindNm")
				val screenKR = showtime.getString("theabExpoNm")
				val screenEN = showtime.getString("theabEngNm")
				val ticketPage = "https://www.megabox.co.kr/bookingByPlaySchdlNo?playSchdlNo=${playSchldNo}"
				
				val restSeat = showtime.getInt("restSeatCnt").toString()
				val startTime = showtime.getString("playStartTime").replace(":", "")
				val endTime = showtime.getString("playEndTime").replace(":", "")
				
				val showTimeKey = Triple(movieKR, screenKR, playKind)
				val item =
					ShowTimeVO.ShowTimeReserveVO(
						(date + startTime).toLong(),
						(date + endTime).toLong(),
						restSeat.toInt(),
						ticketPage
					)
				
				if (dtos[showTimeKey] == null) {
					val items = ArrayList<ShowTimeVO.ShowTimeReserveVO>()
					items.add(item)
					
					val showTimeVO =
						ShowTimeVO(
							screenEN,
							totalSeat,
							items
						)
					dtos[showTimeKey] = showTimeVO
				} else {
					dtos[showTimeKey]?.items?.add(item)
				}
				
			}
			
			val list = retShowTimeList(dtos, date, theaterData)
			showTimeList += (list)
		}
		return showTimeList
	}
	
	fun getLCTicketAddr(data: JSONObject): String {
		val screenID = data.get("ScreenID").toString()
		val cinemaID = data.get("CinemaID").toString()
		val movieCd = data.getString("MovieCode")
		val date = data.getString("PlayDt")
		val startTime = data.getString("StartTime")
		val ticketPage =
			LCurl + "/NLCHS/ticketing?link_screenId=${screenID}&link_cinemaCode=${cinemaID}&link_movieCd=${movieCd}&link_date=${date}&link_time=${startTime}&link_channelCode=naver"
		
		return ticketPage
	}
	
	fun getLCDates(theatercode: String): ArrayList<String> {
		var datelist = ArrayList<String>()
		val url: String =
			LCurl + "/LCWS/Ticketing/TicketingData.aspx"
		
		var paramlist = HashMap<String, Any>()
		paramlist["MethodName"] = "GetInvisibleMoviePlayInfo"
		paramlist["channelType"] = "HO"
		paramlist["osType"] = "W"
		paramlist["osVersion"] = userAgent
		paramlist["cinemaList"] = theatercode
		paramlist["movieCd"] = ""
		paramlist["playDt"] = "2023-03-03"
		val conn = Jsoup.connect(url)
			.userAgent(userAgent)
			.data("ParamList", JSONObject(paramlist).toString())
		val doc = conn.post().body().text()
		val data = JSONObject(doc).getJSONObject("PlayDates").getJSONArray("Items")
		
		for (i in 0 until data.length()) {
			val playdate = data.getJSONObject(i).getString("PlayDate").split(" ")
			datelist.add(playdate[0])
		}
		
		return datelist
	}
	
	fun updateLCShowtimes(
		theaterData: TheaterData
	): List<ShowTime> {
		var showTimeList = ArrayList<ShowTime>()
		val cinemaCode = theaterData.theaterCode
		val datelist = getLCDates(cinemaCode)
		
		for (date in datelist) {
			val url: String =
				LCurl + "/LCWS/Ticketing/TicketingData.aspx"
			var paramlist = HashMap<String, String>()
			paramlist["MethodName"] = "GetPlaySequence"
			paramlist["channelType"] = "HO"
			paramlist["osType"] = "W"
			paramlist["osVersion"] = userAgent
			paramlist["playDate"] = date
			paramlist["cinemaID"] = cinemaCode
			paramlist["representationMovieCode"] = ""
			val conn = Jsoup.connect(url)
				.userAgent(userAgent)
				.data("ParamList", JSONObject(paramlist).toString())
			val doc = conn.post().body().text()
			val data = JSONObject(doc)
			val playSeqs = data.getJSONObject("PlaySeqs").getJSONArray("Items")
			val playHeaders = data.getJSONObject("PlaySeqsHeader").getJSONArray("Items")
			val translation = HashMap<Int, String>()
			
			for (i in 0 until playHeaders.length()) {
				val headerdata = playHeaders.getJSONObject(i)
				val translationCode = headerdata.getInt("TranslationDivisionCode")
				val translationName = headerdata.getString("TranslationDivisionNameKR")
				translation[translationCode] = translationName
			}
			
			val dtos = HashMap<Triple<String, String, String>, ShowTimeVO>()
			for (i in 0 until playSeqs.length()) {
				val playdata = playSeqs.getJSONObject(i)
				
				val movieKR = playdata.getString("MovieNameKR")
				val screenDivisionCode = playdata.getInt("ScreenDivisionCode")
				var screenDivisionKR = ""
				var screenDivisionEN = ""
				if (screenDivisionCode != 100 && playdata.getString("ScreenDivisionNameKR") != playdata.get("ScreenNameKR")) {
					screenDivisionKR = playdata.getString("ScreenDivisionNameKR") + " "
					screenDivisionEN = playdata.getString("ScreenDivisionNameUS") + " "
				}
				val screenKR = screenDivisionKR + playdata.get("ScreenNameKR")
				val screenEN = screenDivisionEN + playdata.get("ScreenNameUS")
				
				val startTime = playdata.getString("StartTime").replace(":", "")
				val endTime = playdata.getString("EndTime").replace(":", "")
				val totalSeat = playdata.getInt("TotalSeatCount")
				val restSeat = playdata.getInt("BookingSeatCount").toString()
				val translationCode = playdata.getInt("TranslationDivisionCode")
				
				var playKind = playdata.getString("FilmNameKR")
				if (translationCode != 900)
					playKind + "(${translation[translationCode]})"
				
				val ticketPage = getLCTicketAddr(playdata)
				
				val showTimeKey = Triple(movieKR, screenKR, playKind)
				val item =
					ShowTimeVO.ShowTimeReserveVO(
						(date.replace("-", "") + startTime).toLong(),
						(date.replace("-", "") + endTime).toLong(),
						restSeat.toInt(),
						ticketPage
					)
				
				if (dtos[showTimeKey] == null) {
					val items = ArrayList<ShowTimeVO.ShowTimeReserveVO>()
					items.add(item)
					
					val showTimeVO =
						ShowTimeVO(
							screenEN,
							totalSeat,
							items
						)
					dtos[showTimeKey] = showTimeVO
				} else {
					dtos[showTimeKey]?.items?.add(item)
				}
				
			}
			
			val list = retShowTimeList(dtos, date.replace("-", ""), theaterData)
			showTimeList += list
		}
		return showTimeList
	}
	
	fun getCGVDates(theatercode: String): ArrayList<String> {
		val url: String =
			CGVurl + "/common/showtimes/iframeTheater.aspx?theatercode=${theatercode}"
		val conn = Jsoup.connect(url)
			.userAgent(userAgent)
			.referrer(
				CGVurl
			)
			.header(
				"Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7"
			)
			.header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
			.header(
				"Cookie",
				"ASP.NET_SessionId=test;"
			)
		val doc = conn.get()
		
		val days = doc.getElementsByClass("day")
		var datelist = ArrayList<String>()
		
		for (day in days) {
			val href = day.getElementsByTag("a")[0].attr("href")
			val pattern: Pattern = Pattern.compile(".*date=([^&]+).*")
			val matcher: Matcher = pattern.matcher(href)
			if (matcher.find()) {
				datelist.add(matcher.group(1))
			}
		}
		
		return datelist
	}
	
	fun chgScreenKRtoEN(screenKR: String): String {
		var screenEN: String = screenKR
		if ("관" in screenKR)
			screenEN = "CINEMA ${screenEN.replace("관", "")}"
		
		return screenEN
	}
	
	fun updateCGVShowtimes(
		theaterData: TheaterData
	): List<ShowTime> {
		var showTimeList = ArrayList<ShowTime>()
		
		val theaterCode = theaterData.theaterCode
		val datelist = getCGVDates(theaterCode)
		
		for (date in datelist) {
			val url: String =
				CGVurl + "/common/showtimes/iframeTheater.aspx?theatercode=${theaterCode}&date=${date}"
			val conn = Jsoup.connect(url)
				.userAgent(userAgent)
				.referrer(
					CGVurl
				)
				.header(
					"Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7"
				)
				.header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
				.header(
					"Cookie",
					"ASP.NET_SessionId=test;"
				)
			val doc = conn.get()
			val showtimes = doc.getElementsByClass("col-times")
			
			val dtos = HashMap<Triple<String, String, String>, ShowTimeVO>()
			for (showtime in showtimes) {
				val infoMovie = showtime.getElementsByClass("info-movie")[0]
				val movieKR = infoMovie.getElementsByTag("a")[0].text()
				
				val typeHalls = showtime.getElementsByClass("type-hall")
				for (typeHall in typeHalls) {
					val infoHall = typeHall.getElementsByClass("info-hall")[0]
					val playKind = infoHall.getElementsByTag("li")[0].text()
					val screenKR = infoHall.getElementsByTag("li")[1].text()
					val screenEN = chgScreenKRtoEN(screenKR)
					val totalSeat =
						infoHall.getElementsByTag("li")[2].text().replace("[^0-9]+".toRegex(), "").toInt()
					
					val infoTimeTable = typeHall.getElementsByClass("info-timetable")[0]
					val timelist = infoTimeTable.getElementsByTag("li")
					
					for (timeinfo in timelist) {
						val datas = timeinfo.getElementsByTag("a")
						
						if (datas.isEmpty() || datas[0].attr("href") == "/") {
							continue
						}
						
						val startTime = datas[0].attr("data-playstarttime")
						val endTime = datas[0].attr("data-playendtime")
						val ticketPage = CGVurl + datas[0].attr("href")
						val restSeat = datas[0].attr("data-seatremaincnt")
						
						val showTimeKey = Triple(movieKR, screenKR, playKind)
						val item =
							ShowTimeVO.ShowTimeReserveVO(
								(date + startTime).toLong(),
								(date + endTime).toLong(),
								restSeat.toInt(),
								ticketPage
							)
						
						if (dtos[showTimeKey] == null) {
							val items = ArrayList<ShowTimeVO.ShowTimeReserveVO>()
							items.add(item)
							
							val showTimeVO =
								ShowTimeVO(
									screenEN,
									totalSeat,
									items
								)
							dtos[showTimeKey] = showTimeVO
						} else {
							dtos[showTimeKey]?.items?.add(item)
						}
						
					}
					
				}
			}
			
			val list = retShowTimeList(dtos, date, theaterData)
			showTimeList += list
		}
		return showTimeList
	}
	
	fun retShowTimeList(
		dtos: Map<Triple<String, String, String>, ShowTimeVO>,
		date: String,
		theaterData: TheaterData
	): List<ShowTime> {
		val ret = ArrayList<ShowTime>()
		
		for (dto in dtos) {
			var (movieKR, screenKR, playKind) = dto.key
			val (screenEN, totalSeat, items) = dto.value
			movieKR = movieKR.replace(square, "").trim()
			
			if (nameMap[movieKR] == null) {
				var movieName = MovieNameInfoVO()
				for (nameInfo in movieNameInfo) {
					val similarity = CalcSimilarity.calcSimilarity(nameInfo.nameKr, movieKR)
					
					if (similarity > 0.7 && movieName.similarity < similarity) {
						movieName.movieId = nameInfo.movieId
						movieName.similarity = similarity
					}
				}
				nameMap[movieKR] = movieName
			}
			var name = nameMap[movieKR]
			
			var movieId: String = ""
			if (name == null || name.movieId.isNullOrEmpty()) {
				movieId = movieSearchService.movieSearchService(movieKR)
				nameMap[movieKR] = MovieNameInfoVO(movieId, 1.0)
			} else {
				movieId = name.movieId !!
			}
			
			var showTime = ShowTime(
				screenKR,
				screenEN,
				date.toLong(),
				totalSeat,
				playKind,
				theaterData,
				time
			)
			showTime.movieData = MovieData(movieId)
			
			val reservations = ArrayList<ShowTimeReserve>()
			for (item in items) {
				reservations.add(
					ShowTimeReserve(
						item.startTime,
						item.endTime,
						item.restSeat,
						time,
						item.ticketPage,
						showTime
					)
				)
			}
			showTime.addReservation(reservations)
			ret.add(showTime)
		}
		
		return ret
	}
}
