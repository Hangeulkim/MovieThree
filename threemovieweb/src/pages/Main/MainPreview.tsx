import React, { useEffect, useState } from 'react';
import { useRecoilValue } from 'recoil';
import SwiperCore, { Navigation, Pagination } from 'swiper';
import { Swiper, SwiperSlide } from 'swiper/react';

import 'swiper/scss';
import 'swiper/scss/navigation';
import 'swiper/scss/pagination';
import movieInfoSelector from '../../Recoil/Selector/movieInfoSelector';
import { onErrorImg } from '../../Util/onErrorImg';

const MainPreview = () => {
    const [steelcuts, setSteelcuts] = useState<string[]>();
    const [trailer, setTrailer] = useState<string[]>();
    const movieinfo = useRecoilValue(movieInfoSelector);
    SwiperCore.use([Navigation, Pagination]);

    const stringToArray = (str: string | null) => {
        if (!str) return undefined;
        const ret = str.replace('[', '').replace(']', '').split(',');
        return ret;
    };

    useEffect(() => {
        if (movieinfo) {
            const steelcuts = stringToArray(movieinfo.steelcuts);
            setSteelcuts(steelcuts);
            const trailer = stringToArray(movieinfo.trailer);
            setTrailer(trailer);
        }
    }, [movieinfo]);

    return (
        <Swiper
            className="previewSwiper"
            slidesPerView={1}
            loop
            pagination={{ clickable: true }}
            navigation
            modules={[Pagination, Navigation]}
            autoplay={{ delay: 3000, disableOnInteraction: false }}
        >
            {movieinfo && !steelcuts && !trailer && (
                <SwiperSlide>
                    <img src={movieinfo.poster || ''} onError={onErrorImg} alt="" />
                </SwiperSlide>
            )}
            {steelcuts &&
                steelcuts.map((steelcut) => (
                    <SwiperSlide key={steelcut}>
                        <img src={steelcut} onError={onErrorImg} alt="" />
                    </SwiperSlide>
                ))}
            {trailer &&
                trailer.map((teaser) => (
                    <SwiperSlide key={teaser}>
                        <iframe
                            title={movieinfo.nameKR}
                            width="100%"
                            height="100%"
                            src={`${teaser}?service=player_share`}
                            allowFullScreen
                            frameBorder="0"
                            scrolling="no"
                            allow="autoplay; fullscreen; encrypted-media"
                        />
                    </SwiperSlide>
                ))}
        </Swiper>
    );
};

export default MainPreview;
