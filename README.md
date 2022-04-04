# SoS 유저 환급액 조회 API

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
  
## 하위는 토큰정보 인가를 통한 요청입니다. jwtTokenFilter를 통해 검증하며 jwtExceptionFilter를 통해 예외를 catch합니다. 
## jwtExceptionFilter에 catch 되지 않고 setAuthentication 하지 않은 상태라면 JwtAuthenticationEntryPoint를 통해 인증 실패를 응답합니다.
  
  3. 내정보 보기 
     - token 검증 후 db에 저장된 정보 반환.

  4. 유저 정보 스크랩.
     - retrofit을 사용하여 비동기 api 호출. (https://codetest.3o3.co.kr/)
     - callback 함수를 통해 json을 가공하여 db적재하고 성공, 요청중, 실패 상태값을 가지고 있는 entity를 생성하여 db에 저장. 
     
  5. 환급액 확인
     - 공제액 한도, 공제액, 환급액에 관한 계산방식을 각각 interface을 정의하여 전략패턴으로 사용.
     - 요청 시 이전에 스크랩 요청에 대한 상태값 검증. 

  
## [참고 사항]

  #### JWT 인증.
  1. aceess token이 만료되면 httponly로 저장된 refresh token cookie로 재발급 요청합니다. 
  2. access token 이 만료되기 전, 재발급 요청을 하면 redis에 저장되어 있는 refresh token이 폐기됩니다. 
     (jwt token의 폐기를 직접 할 수 없으므로 redis에 저장하여 관리.)
  5. 인증 관련된 기능은 spring security에서 관리되며 각 성공 실패에 대한 handler가 있습니다. 
  
  #### api 호출 방식.
  1. 회원가입, 스크랩 저장에 대해 삼쩜삼으로부터 제공된 api의 데이터로 검증하며, 응답시간 지연에 따라
     비동기 요청으로 구현하었습니다. 
  3. 비동기 호출시 각 기능에 대한 응답 값을 db에 저장하여 상태관리를 하였습니다. (pending, failed, complete)
  4. spring context에서의 예외처리는 controllerAdvice를 통해 구현하였고, JwtTokenFilter 예외처리는
     jwtExceptionFilter를 JwtTokenFilter 앞에 두어 try catch하였습니다.
   
------------
# 주관식 문제 

## 2. 이벤트 드리븐 기반으로 서비스를 만들 때 이벤트를 구성하는 방식과 실패 처리하는 방식에 대해 서술해 주세요.
     
     - apache kafka는 서비스간 메세지 브로커역할을 하고, 이벤트를 발행-구독하는 형식으로 구성되어있습니다. 
       또한 각 서비스는 구독한 이벤트에 대해 소비자(consumer) 역할을 합니다.
   
     - 데이터를 전달할 때 서비스에 직접 push하는 방식이 아닌 메세지브로커로 데이터를 전달 후, 그 데이터에 
       관심이 있는 서비스가 consume처리합니다.
     
     - 기존의 모놀리식 구성에서의 트랜잭션 관리는 하나의 DBMS안에서 관리되었지만 이벤트 드리븐 기반 서비스는
       어플리케이션 레벨에서 관리됩니다.  
     
     - 각 서비스는 트랜잭션 실패에 대한 보상 트랜잭션(rollback을 하기 위함)을 각 서비스간 메세지 브로커에 
       관심사로 구독하고 있으며 이벤트 발생 시 consume하여 rollback처리합니다. 
---
## 3. MSA 구성하는 방식에는 어떤 것들이 있고, 그중 선택하신다면 어떤 방식을 선택하실건가요?
  
  #### MSA는 (서비스, DB)가 분산되어있어 api로만 통신하게되어 트랜잭션 관리가 
  #### 모놀리식(모든 서비스가 하나로 뭉쳐진 어플리케이션)구성보다 어려운 것으로 알고있습니다. 
 
  ### 저는 아키텍쳐 2가지를 찾아보았습니다. 
  
  ### 1. 2Phase Commit 방식.
   #####  Coordinator에게 트랜잭션 관리를 맡겨 운영합니다.
   #####  투표단계, 커밋단계 둘로 구성되어 변경사항 요청을 반영합니다. 
    
   #### - 투표단계   
   
    - 서비스 간 상태 변경 가능여부에 대해 투표하고 해당 Row에 lock을 잡습니다. 
    
    - 투표 결과 모두 가능한 상황이면 데이터 변경. 서비스 하나라도 변경 불가라면 트랜잭션을 중단합니다.
    
    - 변경은 하였지만 아직 반영 결과에 lock이 잡힌 상태입니다. 
 
   #### - 커밋단계
    
    - 투표 완료 후 Coordinator가 Commit 메시지를 각 서비스로 전달합니다.
   
    - 각 서비스에서 데이터 변경을 반영합니다. 하지만 모든 서비스가 동시간대에 데이터 반영이 되지는 않습니다.
    
    - 그래서 트랜잭션의 기간이 길어지는만큼 대기시간이 길어지므로 사이클이 짧은 서비스에 사용하는 것을 권장합니다. 
    
    - 또한 기능이 많아질 수록 coordinator혼자 가지는 관리책임도 늘어나 서비스간 결합도가 강해집니다.
  
  ### 2. Saga패턴 방식.
   ##### 서비스간 메세지 브로커로 데이터를 송수신하는 방식입니다.  
   ##### 메세지 브로커는 대표적으로 kafka가 있으며 [producer - consumer] 로 구성되어 있습니다. 
   ##### 트랜잭션을 병렬처리가 아닌 열로 이어져있으며 하나의 서비스 검증 성공 후 트랜잭션을 이어 다음 서비스로 이동하는 것이 특징입니다.
   ##### 트랜잭션 실패시 대응 방안으로는 backward/forward recovery 방식 두가지가 있습니다.
   
   #### - backward recovery
   
    -  방식은 트랜잭션 실패 시 이전에 커밋된 트랜잭션을 모두 rollback시킵니다. 
   
    -  보상 트랜잭션을 통한 rollback을 합니다. 
   
    -  메세지 브로커는 보상 트랜잭션 이벤트들을 관리하며 그 이벤트를 구독하고 있는 서비스가 rollback 처리합니다. 
  
  #### - backward recovery
   
    -  반면에 forward recovery 방식은 실패가 발생한 지점에서부터 rollback하지 않고 정보를 유지하여 계속 앞으로 나아가 처리하는 방식입니다.
 
   
 ### 결론은? 
 
    -  저는 MSA를 통해 각 서비스팀이 책임을 나누어 가지게 될 수 있다는 점이 장점이라고 생각합니다. 
   
    -  개발, 배포에 대한 시간을 줄이고 배포 시 각 서비스별로만 확인하며 관리의 책임이 명확해 진다는 점이 좋은 것 같습니다.
   
    -  선택을 한다면 MSA의 방향성을 최대한 잘 살린 아키텍처라고 생각하는 saga패턴 방식을 선택할 것입니다.
 
 ####  SI기업이었던 이전 회사에 있을 때 배포를 하기위해 전 직원이 밤 늦게까지 남아서 일하는 경우가 종종 있었습니다.
 ####  MSA를 아직 제대로 경험해 보지 못했지만 이전의 불편했던 경험을 되살려 왜 사용되는 건지, 방향성은 무엇인지 이해하며
 ####  위의 아키텍처를 잘 살려 개발을 하고싶습니다.
   
---
## 4. 외부 의존성이 높은 서비스를 만들 때 고려해야 할 사항이 무엇인지 서술해 주세요.
  
    1. 외부 api 서비스를 갑자기 중단해버리면 그에 맞게 대응하기 어렵거나, 대처시간이 오래걸릴 수 있습니다.
    
    2. 크롤링 기능이 있다면 외부 서비스의 html폼 구성 변경으로 인한 오류가 생길 수 있으며 작업을 그에 맞게 다시 해야할 수 있습니다.
   
    3. 통합테스트 시 의존성을 가지고 있는 서비스들을 모두 확인해야 하며 각 서비스간의 조정이 필요합니다. 
  
---
#### 5. 일정이 촉박한 프로젝트 진행 시 코드 컨벤션에 맞춰 개발할 것인지?
  
    네. 회사 규칙에 따라 개발할 것입니다.
   
    예를 들어, 테스트 코드 통과를 해야만 pull request를 할 수 있다고 하면 일정이 촉박하다고 하여 테스트 통과 없이 
    
    merge나 배포를 하였을 때 돌아오는 복구 시간은 테스트 코드 실패후 재작업에 들어가는 것보다 느려질 것입니다. 
   
    서비스에 문제가 생겼을 때 문제해결에 대한 압박감을 극복할 수 있는 사람은 거의 없을 것이기 때문입니다. 
    
    결국 사내 팀원들이나 서비스를 이용하는 고객들 모두에게 피해를 주는 상황이므로 사내 관례에 따라 작업을 할 것입니다.            
  
---
#### 6. 민감정보 암호화 알고리즘에는 어떤것들이 있고, 그중 선택하신다면 어떤것들을 선택하실건가요? 그 이유는?

    대칭, 비대칭 알고리즘이 있는 것으로 알고있습니다.   
  
    하나의 키를 가지고 외부와 주기적으로 주고 받는 데이터는 https 에서 사용하는 것과 같은 비대칭키 알고리즘을 사용할 것입니다.
   
    대칭, 비대칭키 중 외부에 있는 누군가와 암호화된 키로 정보를 주고받을 경우 맨 처음 키를 전달할 방법이 없어 
    
    비밀키를 가지고 있는 서버, 공개키를 가지는 외부 접근자로 구성된 비대칭키 방식을 사용하는것이 적합할 것 같습니다. 
    
    단점으로는 암복호화의 시간이 대칭키 방식보다 길다는 점 정도로 알고있습니다. 
    
    서비스 내에서만 관리되는 데이터는 암복호화의 짧은 시간을 위해 대칭키 암호화 방식을 사용할 것입니다. 
    
    이번 과제에서 jwtToken, psswordEncoder는 비대칭키로, 주민번호는 대칭키로 구현하였습니다.  
------------
  
  
  
  
  
  
  
