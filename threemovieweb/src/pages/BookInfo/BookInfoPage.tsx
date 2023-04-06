import React, { useEffect } from 'react';
import { Box } from '@mui/material';
import QueryString from 'qs';
import { useLocation } from 'react-router-dom';
import '../../style/scss/_bookinfo.scss';
import { useRecoilState } from 'recoil';
import bookMovieFilterAtom from '../../Recoil/Atom/bookMovieFilter';
import bookMovieTheaterFilterAtom from '../../Recoil/Atom/bookMovieTheaterFilter';
import bookBrchFilterAtom from '../../Recoil/Atom/bookBrchFilter';
import BookTheater from './BookTheater';
import BookDate from './BookDate';
import BookShowTime from './BookShowTime';
import BookMovieList from './BookMovieList';

const BookInfoPage = () => {
    const [movieFilter, setMovieFilter] = useRecoilState(bookMovieFilterAtom);
    const [movieTheaterFilter, setMovieTheaterFilter] = useRecoilState(bookMovieTheaterFilterAtom);
    const [brchFilter, setBrchFilter] = useRecoilState(bookBrchFilterAtom);
    const location = useLocation();
    const queryData = QueryString.parse(location.search, { ignoreQueryPrefix: true });

    useEffect(() => {
        if (queryData.movie && typeof queryData.movie === 'string') {
            let arr = Array.from(new Set(movieFilter.concat(queryData.movie)));

            if (arr.length > 3) arr = arr.slice(1, 4);
            setMovieFilter(arr);
        }
        if (queryData.movieTheater && typeof queryData.movieTheater === 'string') {
            let arr = Array.from(new Set(movieTheaterFilter.concat(queryData.movieTheater)));

            if (arr.length > 3) arr = arr.slice(1, 4);
            setMovieTheaterFilter(arr);
        }
        if (queryData.brch && typeof queryData.brch === 'string') {
            let arr = Array.from(new Set(brchFilter.concat(queryData.brch)));

            if (arr.length > 3) arr = arr.slice(1, 4);
            setBrchFilter(arr);
        }
    }, [location]);

    return (
        <Box className="flexbox bookCover">
            <BookMovieList />
            <BookTheater />
            <Box className="bookDateShowTimeCover">
                <BookDate />
                <BookShowTime />
            </Box>
        </Box>
    );
};

export default BookInfoPage;
