package com.example.starlet_be.domains.constellation.service;

import com.example.starlet_be.domains.connection.dto.response.ConnectionDto;
import com.example.starlet_be.domains.connection.dto.response.StarryNightConnectionDto;
import com.example.starlet_be.domains.connection.entity.Connection;
import com.example.starlet_be.domains.connection.repository.ConnectionRepository;
import com.example.starlet_be.domains.constellation.dto.request.ConstellationPositionDto;
import com.example.starlet_be.domains.constellation.dto.request.CreateConstellationDto;
import com.example.starlet_be.domains.constellation.dto.request.UpdateConstellationDto;
import com.example.starlet_be.domains.constellation.dto.response.ArchiveDetailDto;
import com.example.starlet_be.domains.constellation.dto.response.ArchiveDto;
import com.example.starlet_be.domains.constellation.dto.response.ConstellationNameSuggestDto;
import com.example.starlet_be.domains.constellation.dto.response.StarryNightConstellationDto;
import com.example.starlet_be.domains.constellation.entity.Constellation;
import com.example.starlet_be.domains.constellation.repository.ConstellationRepository;
import com.example.starlet_be.domains.diary.entity.Color;
import com.example.starlet_be.domains.diary.entity.Diary;
import com.example.starlet_be.domains.star.dto.request.StarPositionDto;
import com.example.starlet_be.domains.star.dto.request.StarsIdDto;
import com.example.starlet_be.domains.star.dto.response.StarArchiveDetailDto;
import com.example.starlet_be.domains.star.dto.response.StarArchiveDto;
import com.example.starlet_be.domains.star.dto.response.StarryNightStarDto;
import com.example.starlet_be.domains.star.entity.Star;
import com.example.starlet_be.domains.star.repository.StarRepository;
import com.example.starlet_be.domains.user.entity.User;
import com.example.starlet_be.domains.user.repository.UserRepository;
import com.example.starlet_be.exception.CustomException;
import com.example.starlet_be.exception.ErrorCode;
import com.example.starlet_be.openai.service.ModerationService;
import com.example.starlet_be.openai.service.OpenAIService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


/**
 * 별자리 서비스
 * 별자리 생성, 밤하늘 별자리 조회, 별 위치 최신화
 *
 * 미구현 3개 더 남아있음
 * 프론트엔드 요청사항에 따라 수정이 잦을 수 있음
 */
@Service
@RequiredArgsConstructor
public class ConstellationService {
    private final ConstellationRepository constellationRepository;
    private final ConnectionRepository connectionRepository;
    private final StarRepository starRepository;
    private final UserRepository userRepository;
    private final OpenAIService openAIService;
    private final ModerationService moderationService;

    /**
     * 별자리 만들기
     *
     * 사용자가 존재하지 않으면 USER_NOT_FOUND
     * 별이 존재하지 않으면 STAR_NOT_FOUND
     * 선택한 별이 이미 별자리에 소속되어있으면 ALREADY_BELONG_TO_CONSTELLATION, 이 부분은 프론트 단에서 막아주긴 해야함
     * 최초로 별자리를 등록한다면 즉시 대표별자리로 등록
     *
     * @param userDetails 토큰 기반 로그인 정보
     * @param dto 생성할 별자리 기본 정보 -> 별자리 이름, 설명, 별 리스트, 선 리스트
     */
    @Transactional
    public void createConstellation(UserDetails userDetails, CreateConstellationDto dto) {
        // 유저 조회
        User user = userRepository.findByEmailAddress(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        // 유해성 검사
        if(moderationService.moderate(dto.getName()).getResults().get(0).isFlagged()
            || moderationService.moderate(dto.getDescription()).getResults().get(0).isFlagged()) {
            throw new CustomException(ErrorCode.INAPPROPRIATE_CONTENT);
        }

        // 최초 생성 별자리라면 그 별자리를 대표 별자리로
        boolean isRepresentative = false;
        if(constellationRepository.countByUser(user) == 0){
            isRepresentative = true;
        }

        // 별자리 생성
        Constellation constellation = Constellation.builder()
                .user(user)
                .name(dto.getName())
                .description(dto.getDescription())
                .createAt(LocalDate.now())
                .isRepresentative(isRepresentative)
                .x(Math.random())
                .y(Math.random())
                .build();
        constellationRepository.save(constellation);

        // 별들 저장
        for(StarPositionDto starDto : dto.getStars()){
            Star star = starRepository.findById(starDto.getStarId()).orElseThrow(
                    () -> new CustomException(ErrorCode.STAR_NOT_FOUND)
            );
            if(star.getConstellation() != null)
                throw new CustomException(ErrorCode.ALREADY_BELONG_TO_CONSTELLATION);
            star.joinConstellation(constellation);
            star.changePosition(starDto.getX(), starDto.getY());
            starRepository.save(star);
        }

        // 연결 저장
        for(ConnectionDto con : dto.getConnections()){
            connectionRepository.save(Connection.builder()
                            .constellation(constellation)
                            .start(starRepository.findById(con.getStartStarId()).orElseThrow(
                                    () -> new CustomException(ErrorCode.STAR_NOT_FOUND)
                            ))
                            .end(starRepository.findById(con.getEndStarId()).orElseThrow(
                                    () -> new CustomException(ErrorCode.STAR_NOT_FOUND)
                            ))
                            .build()
            );
        }

        // 별자리가 속한 월 저장, 그냥 아무 별이나 가져와서 일기 생성일자 저장

        Star star = starRepository.findByConstellation(constellation).get(0);

        constellation.setBelongDate(star.getDiary().getCreateAt());

        constellationRepository.save(constellation);

    }

    /**
     * 밤하늘 별자리 조회
     *
     * 사용자가 존재하지 않으면 USER_NOT_FOUND
     * 월 입력 오류가 발생했을때 DIARY_INVALID_MONTH -> 일단 같은 뜻의 예외라서 재활용함
     *
     * @param userDetails 토큰 기반 로그인 정보
     * @param year 연도
     * @param month 월
     * @return List<StarryNightConstellationDto> 밤하늘 별자리 리스트
     */
    @Transactional(readOnly = true)
    public List<StarryNightConstellationDto> getStarryNightConstellation(
            UserDetails userDetails, int year, int month
    ) {
        // 유저 불러오기
        User user = userRepository.findByEmailAddress(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        if(month > 12 || month < 1)
            throw new CustomException(ErrorCode.DIARY_INVALID_MONTH);

        if(month % 2 == 0)
            month--;

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(2).minusDays(1);

        // 해당 분기의 별자리 불러오기
        List<Constellation> constellations =
                constellationRepository.findByUserAndBelongDateBetween(user, startDate, endDate);

        List<StarryNightConstellationDto> constellationsInfo = new ArrayList<>();

        for(Constellation con : constellations){
            // 별자리 꺼내오기

            // 별자리 기반으로 별들의 정보 꺼내오기
            List<Star> stars = starRepository.findByConstellation(con);
            List<StarryNightStarDto> starsInfo = new ArrayList<>();
            for(Star star : stars){
                starsInfo.add(StarryNightStarDto.builder()
                                .starId(star.getId())
                                .userId(star.getUser().getId())
                                .color(star.getColor().toString())
                                .date(star.getDiary().getCreateAt().toString())
                                .x(star.getX())
                                .y(star.getY())
                                .build()
                );
            }

            // 별자리 기반으로 선들의 정보 꺼내오기
            List<Connection> connections = connectionRepository.findByConstellation(con);
            List<StarryNightConnectionDto> connectionsInfo = new ArrayList<>();
            for(Connection connection : connections){
                connectionsInfo.add(StarryNightConnectionDto.builder()
                                .connectionId(connection.getId())
                                .startStarId(connection.getStart().getId())
                                .endStarId(connection.getEnd().getId())
                                .build()
                );
            }

            // DTO에 담아 저장
            constellationsInfo.add(StarryNightConstellationDto.builder()
                            .constellationId(con.getId())
                            .userId(con.getUser().getId())
                            .x(con.getX())
                            .y(con.getY())
                            .name(con.getName())
                            .createAt(con.getCreateAt())
                            .belongDate(con.getBelongDate())
                            .stars(starsInfo)
                            .connections(connectionsInfo)
                            .build()
            );
        }


        return constellationsInfo;
    }

    /**
     * 별자리 위치 최신화
     *
     * 별자리를 찾을 수 없으면 CONSTELLATION_NOT_FOUND
     * 범위 밖의 위치 값이 들어오면 CONSTELLATION_POSITION_OUT_OF_SCOPE
     *
     * @param id 별자리ID
     * @param dto 별자리 위치정보 : x, y
     */
    @Transactional
    public void repositionConstellation(Long id, ConstellationPositionDto dto) {

        // 1. 별자리 존재 확인
        Constellation constellation = constellationRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.CONSTELLATION_NOT_FOUND)
        );

        // 2. 좌표가 범위 안인지 검사
        if(dto.getX() < 0 || dto.getX() > 1 || dto.getY() < 0 || dto.getY() > 1)
            throw new CustomException(ErrorCode.CONSTELLATION_POSITION_OUT_OF_SCOPE);

        // 3. 위치 적용
        constellation.changePosition(dto.getX(), dto.getY());

        // 4. 저장
        constellationRepository.save(constellation);

    }

    /**
     * 별자리 아카이브 목록 조회
     *
     * 사용자가 만든 별자리를 모두 조회
     *
     * @param userDetails 사용자 로그인 정보
     * @return 별자리 아카이브 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<ArchiveDto> getArchiveList(UserDetails userDetails){

        // 1. 사용자 조회
        User user = userRepository.findByEmailAddress(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        // 2. 사용자의 별자리 모두 들고오기
        List<Constellation> constellations = constellationRepository.findByUser(user);

        List<ArchiveDto> archiveList = new ArrayList<>();

        for(Constellation con : constellations){
            List<Star> stars = starRepository.findByConstellation(con);
            List<StarArchiveDto> starArchiveList = new ArrayList<>();

            for(Star star : stars){
                starArchiveList.add(StarArchiveDto.builder()
                        .starId(star.getId())
                        .x(star.getX())
                        .y(star.getY())
                        .color(star.getColor().toString())
                        .build());
            }

            List<Connection> connections = connectionRepository.findByConstellation(con);
            List<ConnectionDto> connectionList = new ArrayList<>();

            for(Connection connection : connections){
                connectionList.add(ConnectionDto.builder()
                    .startStarId(connection.getStart().getId())
                    .endStarId(connection.getEnd().getId())
                    .build());
            }

            archiveList.add(ArchiveDto.builder()
                            .constellationId(con.getId())
                            .name(con.getName())
                            .description(con.getDescription())
                            .date(con.getCreateAt())
                            .isRepresentative(con.isRepresentative())
                            .stars(starArchiveList)
                            .connections(connectionList)
                    .build());
        }

        return archiveList;
    }

    /**
     * 별자리 아카이브 페이징 조회
     *
     * 사용자가 만든 별자리를 모두 조회
     *
     * @param userDetails 사용자 로그인 정보
     * @param pageable 페이지 번호와 가져올 개수 지정
     * @return 별자리 아카이브 DTO 리스트
     */
    @Transactional(readOnly = true)
    public Page<ArchiveDto> getArchivePaging(
            UserDetails userDetails,
            Pageable pageable
    ){

        // 1. 사용자 조회
        User user = userRepository.findByEmailAddress(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        // 2. 사용자의 별자리 모두 들고오기
        Page<Constellation> constellations = constellationRepository.findByUser(user, pageable);

        // 3. map을 통한 별자리 아카이브 페이지 목록 반환
        return constellations.map(con -> {
            List<Star> stars = starRepository.findByConstellation(con);
            List<StarArchiveDto> starArchiveList = new ArrayList<>();

            for(Star star : stars){
                starArchiveList.add(StarArchiveDto.builder()
                        .starId(star.getId())
                        .x(star.getX())
                        .y(star.getY())
                        .color(star.getColor().toString())
                        .build());
            }

            List<Connection> connections = connectionRepository.findByConstellation(con);
            List<ConnectionDto> connectionList = new ArrayList<>();

            for(Connection connection : connections){
                connectionList.add(ConnectionDto.builder()
                        .startStarId(connection.getStart().getId())
                        .endStarId(connection.getEnd().getId())
                        .build());
            }

            // 함수 내부에서 결과를 리턴
            return ArchiveDto.builder()
                    .constellationId(con.getId())
                    .name(con.getName())
                    .description(con.getDescription())
                    .date(con.getCreateAt())
                    .isRepresentative(con.isRepresentative())
                    .stars(starArchiveList)
                    .connections(connectionList)
                    .build();
        });
    }

    /**
     * 별자리 아카이브 상세조회
     *
     * 사용자가 클릭한 하나의 별자리에 대해 상세정보를 조회
     *
     * @param id 별자리 id
     * @return 별자리 정보, 감정별 별의 개수 통합 DTO
     */
    @Transactional(readOnly = true)
    public ArchiveDetailDto getArchiveDetail(Long id){

        // 1. 별자리 찾기
        Constellation con = constellationRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.CONSTELLATION_NOT_FOUND)
        );

        List<Star> stars = starRepository.findByConstellation(con);
        List<StarArchiveDetailDto> starArchiveDetailList = new ArrayList<>();

        for(Star star : stars){
            starArchiveDetailList.add(StarArchiveDetailDto.builder()
                    .starId(star.getId())
                    .x(star.getX())
                    .y(star.getY())
                    .color(star.getColor().toString())
                    .date(star.getDiary().getCreateAt())
                    .build());
        }

        List<Connection> connections = connectionRepository.findByConstellation(con);
        List<ConnectionDto> connectionList = new ArrayList<>();

        for(Connection connection : connections){
            connectionList.add(ConnectionDto.builder()
                    .startStarId(connection.getStart().getId())
                    .endStarId(connection.getEnd().getId())
                    .build());
        }

        return ArchiveDetailDto.builder()
                .constellationId(con.getId())
                .name(con.getName())
                .description(con.getDescription())
                .date(con.getCreateAt())
                .isRepresentative(con.isRepresentative())
                .stars(starArchiveDetailList)
                .connections(connectionList)
                .happynessCount(starRepository.countByConstellationAndColor(con, Color.YELLOW))
                .funnyCount(starRepository.countByConstellationAndColor(con, Color.ORANGE))
                .neutralCount(starRepository.countByConstellationAndColor(con, Color.GREEN))
                .surprisingCount(starRepository.countByConstellationAndColor(con, Color.PURPLE))
                .angerCount(starRepository.countByConstellationAndColor(con, Color.RED))
                .sadnessCount(starRepository.countByConstellationAndColor(con, Color.BLUE))
                .build();
    }


    /**
     * 별자리 이름 및 설명 수정 API
     *
     * 별자리 아카이브에서 별자리의 이름과 설명을 수정하는 API 입니다.
     *
     * @param id 수정할 별자리 id 입니다.
     * @param dto 수정할 별자리 정보들 입니다.
     */
    @Transactional
    public void updateConstellationInfo(Long id, UpdateConstellationDto dto){

        // 1. 별자리 찾기
        Constellation con = constellationRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.CONSTELLATION_NOT_FOUND)
        );

        // 2. 유해성 확인
        if(moderationService.moderate(dto.getName()).getResults().get(0).isFlagged()
            || moderationService.moderate(dto.getDescription()).getResults().get(0).isFlagged()){
            throw new CustomException(ErrorCode.INAPPROPRIATE_CONTENT);
        }

        // 3. 정보 수정
        con.updateInfo(dto.getName(), dto.getDescription());
    }

    /**
     * 대표별자리 지정/변경 API
     *
     * Constellation의 boolean필드를 통해 대표별자리 변경을 시도합니다.
     * 이전 별자리의 대표를 해제하고 새로운 별자리를 대표로 등록합니다.
     *
     * @param id 새로 대표로 만들 별자리의 id 입니다.
     * @param userDetails 유저 정보 입니다.
     */
    @Transactional
    public void changeRepresentativeConstellation(Long id, UserDetails userDetails) {

        // 1. 사용자 찾기
        User user = userRepository.findByEmailAddress(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        // 2. 별자리 찾기
        Constellation after = constellationRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.CONSTELLATION_NOT_FOUND)
        );

        // 3. 이전에 대표별자리였던것 불러오기
        Constellation prev = constellationRepository.findByUserAndIsRepresentative(user, true).orElse(null);

        // 4. 만약 대표별자리가 있었을경우 대표별자리 취소
        if(prev != null){
            prev.changeRepresentative();
            constellationRepository.save(prev);
        }

        // 5. 대표별자리 지정
        after.changeRepresentative();
        constellationRepository.save(after);

    }

    // 별들의 번호만을 가져와서 나머지 싹 다 조회하도록 구현해보기
    @Transactional
    public ConstellationNameSuggestDto suggestConstellationName(StarsIdDto dto){
        List<Long> starIds = dto.getStarIds();

        StringBuilder starsInfo = new StringBuilder();
        for(Long starId : starIds){
            Star star = starRepository.findById(starId).orElseThrow(
                    () -> new CustomException(ErrorCode.STAR_NOT_FOUND)
            );
            Diary diary = star.getDiary();

            starsInfo.append("emotion : " + diary.getEmotion() + ", factors : " + diary.getFactors() + ", content : " + diary.getContent() + "\n");

        }

        String sysPrompt = """
                    별자리의 이름과 설명을 정하려고 합니다.
                    주어지는 정보는 별과 연결된 일기의 감정, 요인들, 내용입니다.
                    별들의 느낌을 기반으로 별자리의 이름과 별자리의 설명을 상세히 적어주셔야 합니다.
                    
                    출력하는 형식은 반드시 다음과 같이 slash(/)로 구분하여 분리하여 주세요.
                    구분자 사이에 공백이나 다른건 절대 없어야합니다.
                    name과 description은 맨 앞과 맨 뒤에 공백문자가 오면 안된다는 뜻입니다.
                    
                    <name>/<description>
                    
                    예시1)
                    물병자리/물병자리이다.
                    
                    예시2)
                    카시오페아자리/카시오페아자리이며, 아름답다.
                    
                    name 필드는 사용자의 일기 작성 언어에 따라 언어를 지정해 이름을 지어주면 됩니다.
                    description필드는 사용자의 일기들과 감정, 요인들을 종합평가하여 함축되고 추상적인 설명을 붙여 감성적이게 표현해주세요.
                    
                    별자리 이름은 공백 포함 10자 이내여야 합니다. 그리고 2자 이상 생성을 권장합니다.
                    예를들어 "물병자리", "오징어자리", "constella" 는 가능합니다.
                    "불", "가나다라마바사아자차카타파하", "constellation" 는 불가능합니다.
                    별자리 설명은 공백 포함 30자 이내여야 합니다. 그러나 설명이 너무 부실하게 적으면 안됩니다.
                    """;

        String[] result = openAIService.getAssistance(starsInfo.toString(), sysPrompt).split("/", -1);

        if(result.length != 2)
            throw new CustomException(ErrorCode.OPENAI_SERVER_ERROR);

        return ConstellationNameSuggestDto.builder()
                .name(result[0])
                .description(result[1])
                .build();

    }
}
