# MovieThree
## 사이트 소개
![메인](https://github.com/Hangeulkim/MovieThree/assets/41067036/bae4cae2-ffca-4c08-8f2a-0bf5be1d860f)
![시간표](https://github.com/Hangeulkim/MovieThree/assets/41067036/e9a1429e-8624-4ed9-98c4-3df4dd7d97de)
![로그인](https://github.com/Hangeulkim/MovieThree/assets/41067036/d79f367e-fa14-4ed8-a41b-ad0b4144b592)

영화관 CGV, Lotte Cinema, MegaBox의 시간표와 남은 좌석 수를 보여줍니다.

- 메인, 로고 - 메인 화면으로 이동합니다.
- 영화 목록 - 화면에 보이는 것 뿐이 아닌 현재 상영 중인 최근 개봉작들을 모두 보여줍니다.
- 영화관 목록 - 영화관 별 시간표를 확인할 수 있습니다.
- 시간표 목록 - 영화 테마, 감독, 배우 검색 및 영화관 검색이 가능합니다.


## 대략적인 서비스 구조
프론트와 백엔드의 상세 설명은 내부에 따로 존재합니다.

![웹서비스](https://github.com/Hangeulkim/MovieThree/assets/41067036/cae4398b-c330-4090-aa90-c25067f88085)
1. http로 접속시 nginx가 https로 리다이렉트 시킵니다.
2. https로 접속 했다면 nginx가 location을 통해 /api 요청인지 프론트 요청인지를 판별합니다.
3. location /api 라면 localhost:8080으로 proxy pass를 해줍니다.
4. location /라면 localhost:3000으로 proxyt pass를 해줍니다.

![jenkins](https://github.com/Hangeulkim/MovieThree/assets/41067036/a84c6411-f9bd-4619-9531-855d5bc062a0)
1. GitHub main push 시 Git Action으로 GitLab에 push를 동일하게 날립니다.
2. GitLab main에 push 가 들어올 경우 Jenkins에 push가 왔다는 알림을 보냅니다.
3. Jenkins 에서 spring build와 node build를 실행 합니다.
4. build된 백 엔드 서버는 docker file을 읽어 docker로 실행합니다.
5. 프론트 서버의 경우 build가 되면 nodemon이 걸려 있는 폴더로 이동합니다.
6. nodemon이 프론트의 변경점을 확인하고 반영 해줍니다.

![전체구조](https://github.com/Hangeulkim/MovieThree/assets/41067036/db536330-f89b-40d1-a5b9-f7352324a270)

#### 사용된 스택
```
Nas : DS220+
OS: DSM 7.1.1
```

###### Docker
```
nginx:1.9.15 - alpine 
node: 16.20.0
jenkins:jdk17
redis
mariaDB 10
```

###### Back End
```
Spring-Boot: 3.0.1
jdk: 17
Kotlin: 1.7.22
Jsoup:1.15.4
querydsl:5.0.0:jakerta
coroutine:1.7.1
```

###### Front End
```
Npm: 16.20.0
React
Typescript
```

###### 사용되는 DB
```
Jwt관리 용 Redis
일반적인 정보 저장 용 MariaDB
```

## 제작기
MovieThree 제작(https://hdobby.tistory.com/118)
