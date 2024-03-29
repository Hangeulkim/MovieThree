import React, {useEffect, useState} from 'react';
import {Box, Button, Input, InputAdornment, ToggleButton, ToggleButtonGroup, Typography} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import {useRecoilState, useRecoilValue} from 'recoil';
import {GET_THEATER_LIST} from '../../gql/showtime.gql';
import bookBrchFilterAtom from '../../Recoil/Atom/bookBrchFilterAtom';
import bookMovieTheaterFilterAtom from '../../Recoil/Atom/bookMovieTheaterFilterAtom';
import bookDateFilterAtom from '../../Recoil/Atom/bookDateFilterAtom';
import bookMovieFilterAtom from '../../Recoil/Atom/bookMovieFilterAtom';
import CGV from '../../assets/images/CGV.png';
import LC from '../../assets/images/LotteCinema.png';
import MB from '../../assets/images/MegaBox.png';
import useGraphQL from "../../hook/useGraphQL";
import {BookBrch, TheaterItem} from "../../interfaces/BookShowTime";

const BookBrchList = () => {
	const [theaters, setTheaters] = React.useState(() => ['CGV', 'LC', 'MB']);
	const [activeCity, setActiveCity] = useState<string>('서울');
	const [search, setSearch] = useState<string>('');
	const [theaterList, setTheaterList] = useState<BookBrch[] | undefined>(undefined);
	const [movieTheaterFilter, setMovieTheaterFilter] = useRecoilState(bookMovieTheaterFilterAtom);
	const [brchFilter, setBrchFilter] = useRecoilState(bookBrchFilterAtom);
	const dateFilter = useRecoilValue(bookDateFilterAtom);
	const movieFilter = useRecoilValue(bookMovieFilterAtom);
	const {data, refetch} = useGraphQL({query: GET_THEATER_LIST});
	
	const onChangeSearch = (event: React.ChangeEvent<HTMLTextAreaElement | HTMLInputElement>) => {
		setSearch(event.target.value);
	};
	
	const onChangeTheater = (event: React.MouseEvent<HTMLElement>, newTheaters: string[]) => {
		setTheaters(newTheaters);
	};
	
	const onChangeCity = (city: string) => {
		setActiveCity(city);
	};
	
	const onClickBrch = (movieTheater: string, brch: string) => {
		for (let i = 0; i < movieTheaterFilter.length; i += 1) {
			if (brchFilter[i] === brch && movieTheaterFilter[i] === movieTheater) {
				let arr = movieTheaterFilter.filter((item, idx) => idx !== i);
				setMovieTheaterFilter(arr);
				arr = brchFilter.filter((item, idx) => idx !== i);
				setBrchFilter(arr);
				return;
			}
		}
		if (movieTheater) {
			let arr = Array.from(movieTheaterFilter.concat(movieTheater));
			
			if (arr.length > 3) arr = arr.slice(1, 4);
			setMovieTheaterFilter(arr);
		}
		if (brch) {
			let arr = Array.from(brchFilter.concat(brch));
			
			if (arr.length > 3) arr = arr.slice(1, 4);
			setBrchFilter(arr);
		}
	};
	
	const onTheaterImg = (movieTheater: string) => {
		if (movieTheater === 'CGV') return CGV;
		if (movieTheater === 'LC') return LC;
		return MB;
	};
	
	const onSearchCity = (theaters: BookBrch) => {
		for (let i = 0; i < theaters.items.length; i += 1) {
			const theater = theaters.items[i];
			if (onSearchTheater(theater)) return true;
		}
		return false;
	};
	
	const onSearchTheater = (theater: TheaterItem) => {
		if (!theaters.includes(theater.movieTheater)) return false;
		if (theater.movieTheater.toUpperCase().search(search) !== -1) return true;
		if (theater.brchKr.search(search) !== -1) return true;
		if (theater.brchEn && theater.brchEn.toUpperCase().search(search.toUpperCase()) !== -1) return true;
		if (theater.addrKr.search(search) !== -1) return true;
		if (theater.addrEn && theater.addrEn.toUpperCase().search(search.toUpperCase()) !== -1) return true;
		return false;
	};
	
	const chkBrchFilter = (movieTheater: string, brch: string) => {
		for (let i = 0; i < movieTheaterFilter.length; i += 1) {
			if (movieTheater === movieTheaterFilter[i] && brch === brchFilter[i]) return true;
		}
		return false;
	};
	
	const chkSearchNone = () => {
		if (
			theaterList &&
			theaterList.find((item) => item.city === activeCity) &&
			theaterList.find((item) => item.city === activeCity)!.items.filter((item) => onSearchTheater(item))
				?.length > 0
		)
			return true;
		return false;
	};
	
	useEffect(() => {
		refetch();
	}, [dateFilter, movieFilter]);
	
	useEffect(() => {
		if (data) {
			setTheaterList(data.getTheaterList);
		}
	}, [data]);
	
	return (
		<Box className="bookTheaterCover">
			<Box>
				<Input
					disableUnderline
					className="searchField"
					placeholder="영화관 정보(이름, 주소)를 입력 해주세요."
					endAdornment={
						<InputAdornment position="end">
							<SearchIcon/>
						</InputAdornment>
					}
					value={search}
					onChange={onChangeSearch}
					inputProps={{
						maxLength: 30,
					}}
				/>
			</Box>
			
			<ToggleButtonGroup value={theaters} onChange={onChangeTheater}>
				<ToggleButton value="CGV">
					<img src={onTheaterImg("CGV")} alt="CGV"/>
				</ToggleButton>
				<ToggleButton value="LC">
					<img src={onTheaterImg("LC")} alt="Lotte Cinema"/>
				</ToggleButton>
				<ToggleButton value="MB">
					<img src={onTheaterImg("MB")} alt="Mega Box"/>
				</ToggleButton>
			</ToggleButtonGroup>
			
			<Box className="cityBrchCover">
				<Box className="cityCover">
					{theaterList &&
						theaterList
							.filter((theaters) => onSearchCity(theaters))
							.map((theater) => (
								<Button
									className={activeCity === theater.city ? 'active city' : 'city'}
									key={theater.city}
									onClick={() => onChangeCity(theater.city)}
								>
									<Typography>{theater.city}</Typography>
								</Button>
							))}
				</Box>
				
				<Box className="brchCover">
					{chkSearchNone() ? (
						theaterList!
							.find((item) => item.city === activeCity)
							?.items!.filter((item) => onSearchTheater(item))
							.map((brch, idx) => (
								<Button
									className={chkBrchFilter(brch.movieTheater, brch.brchKr) ? 'active brch' : 'brch'}
									key={brch.brchKr.concat(idx.toString())}
									onClick={() => onClickBrch(brch.movieTheater, brch.brchKr)}
								>
									<Typography className="brchName">{brch.brchKr}</Typography>
									<img
										className="theaterImg"
										src={onTheaterImg(brch.movieTheater)}
										alt={brch.movieTheater}
									/>
								</Button>
							))
					) : (
						<Typography className="searchNone">검색 결과가 없습니다.</Typography>
					)}
				</Box>
			</Box>
			
			<Box className="filterCover">
				{movieTheaterFilter &&
					brchFilter &&
					movieTheaterFilter.map((movieTheater, idx) => (
						<Box
							onClick={() => onClickBrch(movieTheater, brchFilter.at(idx)!)}
							key={`${brchFilter.at(idx)}${movieTheater}`}
							className="filterBox"
						>
							<Box className="movieTheaterFilter">
								<img src={onTheaterImg(movieTheater)} alt={movieTheater}/>
							</Box>
							<Box className="brchFilter">
								<Typography className="brchFilterName">{brchFilter.at(idx)}</Typography>
							</Box>
						</Box>
					))}
			</Box>
		</Box>
	);
};

export default BookBrchList;
