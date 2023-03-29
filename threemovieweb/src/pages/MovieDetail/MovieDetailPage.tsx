import React from 'react';
import { TabContext, TabList, TabPanel } from '@mui/lab';
import { Box, Divider, Tab } from '@mui/material';
import QueryString from 'qs';
import { useLocation } from 'react-router-dom';
import { useRecoilState } from 'recoil';
import useAxios from '../../assets/hook/useAxios';
import MovieDetail from '../../interfaces/MovieDetail';
import movieDetailTabAtom from '../../Recoil/Atom/movieDetailTabAtom';
import '../../style/scss/_moviedetail.scss';
import Loading from '../Loading';
import MovieCreator from './MovieCreator';
import MovieHeader from './MovieHeader';
import MovieMakingNote from './MovieMakingNote';
import MoviePreview from './MoviePreview';
import MovieReview from './MovieReview';

const MovieDetailPage = () => {
    const [value, setValue] = useRecoilState(movieDetailTabAtom);
    const location = useLocation();
    const queryData = QueryString.parse(location.search, { ignoreQueryPrefix: true });
    const { response, loading, error } = useAxios<MovieDetail>({
        method: 'get',
        url: `/api/movieinfo/moviedetail/${queryData.movie}`,
        config: {
            headers: { 'Content-Type': `application/json` },
        },
    });

    const handleChange = (event: React.SyntheticEvent, newValue: string) => {
        setValue(newValue);
    };

    if (loading) <Loading />;
    return (
        <Box className="flexbox detailCover">
            <MovieHeader
                movieId={response?.movieId}
                nameKR={response?.nameKR}
                nameEN={response?.nameEN}
                netizenAvgRate={response?.netizenAvgRate}
                releaseDate={response?.releaseDate}
                reservationRate={response?.reservationRate}
                category={response?.category}
                poster={response?.poster}
                runningTime={response?.runningTime}
                admissionCode={response?.admissionCode}
                country={response?.country}
                reservationRank={response?.reservationRank}
                totalAudience={response?.totalAudience}
            />
            <Divider className="divide" variant="middle" />
            <TabContext value={value}>
                <Box className="tabCover">
                    <TabList value={value} onChange={handleChange}>
                        {(response?.summary || response?.makingNote) && <Tab label="상세 정보" value="detail" />}
                        {response?.items && <Tab label="감독/배우" value="creator" />}
                        {(response?.steelcuts || response?.trailer) && <Tab label="미리보기" value="preview" />}
                        <Tab label="리뷰" value="review" />
                    </TabList>
                </Box>
                <TabPanel value="detail">
                    <MovieMakingNote />
                </TabPanel>
                <TabPanel value="creator">
                    <MovieCreator />
                </TabPanel>
                <TabPanel value="preview">
                    <MoviePreview />
                </TabPanel>
                <TabPanel value="review">
                    <MovieReview />
                </TabPanel>
            </TabContext>
        </Box>
    );
};

export default MovieDetailPage;
