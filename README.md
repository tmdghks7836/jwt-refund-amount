# JWT를 사용한 유저 환급액 조회 API

------------
#  dependencies
  + lombok
  + jpa
  + spring-boot-starter-test
  + querydsl-jpa
  + springfox-swagger
  + h2database
  + mapstruct
  + spring-security
  + embedded-redis (refresh token 저장)
  + retrofit2
  + okhttp3:logging-interceptor (retrofit 요청 시 로그 확인)
------------

# swagger API

+  /swagger-ui/index.html
+  swagger page의 오른쪽 상단 authorize 버튼을 통해 토큰을 헤더에 전역으로 담아 요청.
+  HttpAuthenticationScheme.JWT_BEARER_BUILDER 를 사용하여 "Bearer " 문자열은 자동 추가되는 것으로 설정.

###  SzsMemberController (유저 환급액 조회)

  + POST /szs/signup           회원가입
  + POST /szs/login            유저 로그인
  + GET /szs/me                내정보 보기
  + POST /szs/scrap            유저 정보 스크랩
  + GET /szs/refund            유저 환급액 계산 정보 조회
  + GET /szs/token/re-issuance 액세스 토큰 재발급

# 요구사항 구현

  1. 회원가입
     - 스크랩 api 비동기 호출 후 응답받은 데이터를 요청한 데이터와 비교하여 검증한 후 db에 저장.
     - 회원가입 실패 알림 기능을 활용할 수 있는 방안이 없어 (이메일 정보 없음.) 특정 시간동안 회원가입 실패를 확인 할 수 있는 상태 이벤트 정보를 db에 저장.
     - BcryptEncoder를 사용하여 비밀번호 저장. 
     - 대칭키 방식을 사용하여 주민번호 정규식 검증 후 저장.
    
  2. 로그인
     - Jwt Token 응답 방식으로 구현하였으며 spring security 사용.
     - 요청 인자값 request body사용. (security attemptAuthentication 함수 호출 시 form login 방식이 아닌 body json 추출.)
     - CustomAuthenticationSuccessHandler 를 통하여 access Token 발급. 
     - 로그인 인증 실패 시 authentication failure handler를 통해 응답.
     - 토근발급시 HMAC-SHA algorithms 을 활용하여 토큰 생성. 
  
  3. 내정보 보기 
     - filter를 통한 token 검증 후 db에 저장된 정보 반환.

  4. 유저 정보 스크랩.
     - filter를 통한 token 검증 후 retrofit을 사용하여 스크랩 api 비동기 요청.
     - callback 함수를 통해 json을 가공하여 db적재하고 성공, 요청중, 실패 상태값을 가지고 있는 entity를 생성하여 db에 저장. 
     
  5. 환급액 정보 
     - filter를 통한 token 검증.
     - 공제액 한도, 공제액, 환급액에 관한 계산방식을 각각 interface을 정의하여 전략패턴으로 사용. (계산 정책변경에 대한 이해도에 따라 다르게 구현했을 것 같습니다.)
     - 요청 시 이전에 스크랩 요청에 대한 상태값 검증. 
     - 숫자 -> 한글 화폐단위 변환 직접 작성.
  
## [참고 사항]

  #### JWT 인증.
  1. aceess token이 만료되면 httponly로 저장된 refresh token cookie로 재발급 요청합니다. 
  2. access token 이 만료되기 전, 재발급 요청을 하면 redis에 저장되어 있는 refresh token이 폐기됩니다. 
     ( refresh token 폐기 처리시 만료기한을 직접 수정 할 수 없으므로 redis에 저장하여 관리.)
  3. jwtTokenFilter를 통해 검증하며 jwtExceptionFilter를 통해 예외를 catch합니다. 
  4. jwtExceptionFilter에 catch 되지 않고 setAuthentication 하지 않은 상태라면 JwtAuthenticationEntryPoint를 통해 인증 실패를 응답합니다.
  
  
  #### api 호출 방식.
  1. 회원가입, 스크랩 저장에 대해 삼쩜삼으로부터 제공된 api의 데이터로 검증하며, 응답시간 지연에 따라
     비동기 요청으로 구현하었습니다. 
  3. 비동기 호출시 각 기능에 대한 응답 값을 db에 저장하여 상태관리를 하였습니다. (pending, failed, complete)
  4. callback 함수 호출 후 트랜잭션 처리 시 TransactionTemplate 을 사용하여 직접 관리하였습니다. 
  5. spring context에서의 예외처리는 controllerAdvice를 통해 구현하였고, JwtTokenFilter 예외처리는
     jwtExceptionFilter를 JwtTokenFilter 앞에 두어 try catch하였습니다.
   

  
  
  
  
  
  
