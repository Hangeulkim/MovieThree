import React, {useEffect} from 'react';
import {Box} from '@mui/material';
import {useSetRecoilState} from 'recoil';
import useAxios from '../../hook/useAxios';
import {MovieData} from '../../interfaces/MovieData';
import movieListAtom from '../../Recoil/Atom/movieLIstAtom';
import movieNowAtom from '../../Recoil/Atom/movieNowAtom';
import '../../style/scss/_mainpage.scss';
import MainMovieList from './MainMovieList';
import MainPreview from './MainPreview';

const MainPage = () => {
	const setMovieList = useSetRecoilState(movieListAtom);
	const setMovieNow = useSetRecoilState(movieNowAtom);
	const [{response}, refetch] = useAxios<MovieData[]>({
		method: 'get',
		url: '/movie/main',
		config: {
			headers: {'Content-Type': `application/json`},
		},
	});
	
	useEffect(() => {
		refetch();
	}, []);
	
	useEffect(() => {
		if (response && response[0]) {
			setMovieList(response);
			setMovieNow(response[0].movieId);
			console.log(response);
		}
	}, [response]);
	
	return (
		<Box className="flexbox">
			<MainPreview/>
			<MainMovieList/>
		</Box>
	);
};

export default MainPage;
