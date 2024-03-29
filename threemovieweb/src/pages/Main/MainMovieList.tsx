import React from 'react';
import {useRecoilValue, useSetRecoilState} from 'recoil';
import SwiperCore, {Navigation} from 'swiper';
import {Swiper, SwiperSlide} from 'swiper/react';

import 'swiper/scss';
import 'swiper/scss/navigation';
import movieListAtom from '../../Recoil/Atom/movieLIstAtom';
import movieNowAtom from '../../Recoil/Atom/movieNowAtom';
import MovieProfile from '../../layout/MovieProfile';

const MainMovieList = () => {
	const setMovieNow = useSetRecoilState(movieNowAtom);
	const movieList = useRecoilValue(movieListAtom);
	
	const handleMovieNow = (index: number) => {
		if (typeof movieList[index] !== "undefined")
			setMovieNow(movieList[index].movieId);
	};
	
	SwiperCore.use([Navigation]);
	return (
		<Swiper
			onSlideChange={(swiper) => handleMovieNow(swiper.realIndex)}
			style={{paddingLeft: '40px', paddingRight: '40px'}}
			className="listSwiper"
			slideToClickedSlide
			loop
			slidesPerView={4}
			breakpoints={{
				1024: {
					slidesPerView: 4,
				},
				1460: {
					slidesPerView: 5,
				},
				1980: {
					slidesPerView: 6,
				},
			}}
			navigation
			modules={[Navigation]}
			centeredSlides
		>
			{movieList.map((movieData, index) => (
				<SwiperSlide key={movieData.movieId}>
					<MovieProfile
						movieId={movieData.movieId}
						poster={movieData.poster}
						nameKr={movieData.nameKr}
						reservationRate={movieData.reservationRate}
						netizenAvgRate={movieData.netizenAvgRate}
						index={index}
					/>
				</SwiperSlide>
			))}
		</Swiper>
	);
};

export default MainMovieList;
