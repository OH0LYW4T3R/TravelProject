# ✈Adventure Time Backend Project✈
### 대회 : 12기 멋쟁이사자 중앙해커톤
### 개발 기간 : 2024/07/15 ~ 2024/08/06 (약 3주)

# Contents
- [1. 프로젝트 개요](#프로젝트-개요)
- [2. 개발 개요](#개발-개요)
- [3. Postman 작업 결과](#Postman-작업-결과)

## 프로젝트 개요
- ### 프로젝트 이름
  - ### ADVENTURE TIME
    #### AI페르소나와 함께하는 맞춤형 여행, 여행 메이트와 함께하는 즐거운 여행
- ### 프로젝트 소개
   Adventure Time은 자신의 여행 성향을 반영한 AI 페르소나와의 상호작용을 통해 맞춤형 여행 정보를 제공하고, 새로운 여행 메이트를 매칭해주는 플랫폼이다.
  지루한 일상에서 벗어나 정신적 건강을 증진시키고, 소셜 헬스를 개선하며, 현지 가이드와의 연계를 통해 지역 특색을 살린 오프라인 체험을 하게 한다.
  #### 주기능
    - #### 여행 페르소나 - 자기이해를 도와주는 맞춤형 여행
        여행 페르소나는 사용자의 여행 성향을 분석하여, 전 세계 16개 도시 중 고유의 아이덴티티를 지닌 하나의 페르소나를 부여한다. 이를 통해 사용자의 특성과 결합된 최적의 여행 제안을 제공한다.
        사용자의 여행 정체성을 담은 여행 페르소나와 상호작용함으로써, 나만을 위한 맞춤형 여행을 경험할 수 있도록 돕는다.
    - #### 여행 메이트 & 현지 가이드 - 최적의 메이트와 함깨하는 여행
        Adventure Time의 메이트 탭은 사용자에게 여행의 새로운 차원을 열어준다. AI 페르소나를 통해 최적의 여행 메이트를 추천하고, 함께 여행을 계획하며 소중한 추억을 정리할 수 있는 기능을 제공한다. 이를 통해 사용자가 더 풍성하고 의미 있는 여행 경험을 누릴 수 있도록 돕는다.
    - #### 여행 일기 - 추억을 간직하는 나만의 여행
        여행일기 탭은 사용자가 여행을 마친 후, 여행의 추억을 생생하게 기록하고 분석할 수 있는 특별한 공간을 제공한다. AI 페르소나와의 대화 상호작용을 통해 여행을 종합적으로 정리하고, 기억에 남는 순간을 일기 형태로 보존할 수 있도록 돕는다.
        AI 페르소나와 함께하는 여행일기를 통해 사용자의 여행을 더욱 특별하게 만든다.
  
- ### 프로젝트 프로토 타입
![이미지1-removebg-preview](https://github.com/user-attachments/assets/47587113-198b-463b-ba49-cc70a6c9f5e7)
![이미지2-removebg-preview](https://github.com/user-attachments/assets/198cb20d-3a15-4dca-8b1f-7e5ec2051c41)
![이미지3-removebg-preview](https://github.com/user-attachments/assets/b4e1a2f6-a437-42f2-a54e-771c281a3835)

## 개발 개요
- ### 맡은 역할
  ### 백엔드
  프론트에서 직접 페이지를 렌더링 하는 CSR방식을 사용하기 위해 REST API방식을 사용하였고, 로그인, 유저 정보, 장바구니 관리, 친구 관리, 현지 가이드 관리, 현지 가이드 제안 관리, 여행, 여행 루트 관리 백엔드 서버를 만들었으며 총 58개 엔드포인트 개발, 또한 각 객체에 대한 정보를 저장해야 하므로 객체에 따른 8개의 Entity와 각 Entity와의 관계 설정, 데이터베이스 설계(데이터베이스는 호스팅 받아 사용)
- ### 기술 스택
  #### 백엔드 프레임워크
  <img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white"> <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> <img src="https://img.shields.io/badge/springsecurity-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white">
  #### 로그인
  <img src="https://img.shields.io/badge/jsonwebtokens-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white">
  
  #### 데이터베이스
  <img src="https://img.shields.io/badge/postgresql-4169E1?style=for-the-badge&logo=postgresql&logoColor=white">
  
  #### API 테스팅 툴
  <img src="https://img.shields.io/badge/postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white">

- ### 주요 개발 기능
  - #### 로그인
    JWT 토큰과 SpringSecurity를 이용한 로그인 방식을 이용하였으며, jsonwebtoken 라이브러리를 이용해, 토큰 발급, 토큰 재발급, 토큰 내용 추출, 쿠키에 토큰 등록, 인증 설정 구현
    또한 Spring의 필터링 기능을 사용하여, 몇개의 엔드포인트를 제외한 나머지는 모두 인증을 거쳐 인증 객체에 등록
    기존 세션방식은 서버에 세션정보를 모두 기록해야 하므로 서버의 부담이 크기때문에 서버에 부담이 적은 JWT 방식 사용.
    비밀번호의 저장은 전부 BCrypt로 암호화
  - #### 유저 정보
    기본적으로 필요한 개인정보와, 장바구니, 가이드, 친구, 제안 등에 대한 데이터 베이스 설계, Jakarta의 persistance 라이브러리를 이용해 Entity와 데이터베이스 간의 연결, 구현
    유저는 가이드가 될 수 있으며, 여행의 기록에 대해 관리 가능
  - #### 장바구니 관리
    유저는 가이드가 낸 여행루트나, 여행 상품을 등록하여 구매할 수 있으며(실제 구매 로직은 미구현) 상태 (등록, 결정대기, 결제대기, 취소, 구매 완료, 여행 완료)에 따른 로직을 분할하여 처리
    결정대기에서는 다시 등록을 할 수 없게 한다 혹은 취소된 상품은 1일뒤에 재등록 된다는 예외같이 발생할 수 있는 예외들에 대해 처리 로직 구현
  - #### 친구 관리
    유저는 친구를 맺을 수 있으며, AI 서버와 통신하여 취향이 맞는 친구를 추천해주고 추천 된 친구와 함께 여행을 갈 수 있다. 또한 상대방을 차단할 수 있는 차단기능 구현 친구 상태 (수락, 거절, 대기, 요청, 차단)에 따라 로직을 분할하여 처리 하였으며, 거절이된 상태면 친구 추가를 못하게 한다던가,
    대기 상태에 진입하면 중복으로 친구 추가를 할 수 없게 하는 등의 예외를 처리
  - #### 현지 가이드 관리
    유저 생성시 가이드란이 자동으로 생기게 되며, 현지 가이드에 대한 역할 또한 수행 할 수 있다. 가이드는 상품을 제시할 수 있으며, 그에 따른 상품 사용자가 평점과 리뷰를 등록 할 수 있으며, 가이드에 대한 평점과 리뷰에 대한 관리를 구현
  - #### 현지 가이드 제안 관리
    가이드는 상품을 제안 할 수 있으며, 그 상품에 대해 상품 사용자의 장바구니에 들어가므로, 상태에 따른 자동 재설정(ex 등록된 상품은 다른 사용자에게 보여주지 않는다)을 위해 필요한 정보들을 관리하고 저장.
  - #### 여행 관리
    사용자는 자신이 갔던 여행에 대해 기록하고, 기록한 내용들을 전부 볼 수 있으며, 어디를 갔고, 언제 갔으며, 누구와 여행을 갔는지에 대한 정보들을 관리하고 저장.
  - #### 여행 루트 관리
    AI를 이용해 추천된 경로를 받아오고 추천된 경로를 사용자에게 보여주며, 사용자가 선택한 추천 경로를 관리하고 저장.
    
## Postman 작업 결과
![image](https://github.com/user-attachments/assets/632f6a5f-c525-4e13-a3c5-d543a76cfebe)
![image](https://github.com/user-attachments/assets/94def073-ad0a-49ed-9bcf-81994e4716f3)
![image](https://github.com/user-attachments/assets/e7a6e68b-abff-45a2-9896-ca42f8f0d9ab)
![image](https://github.com/user-attachments/assets/50afb4d6-9324-446f-a723-f2b4a4c0ccdc)
![image](https://github.com/user-attachments/assets/b68f85e2-12c0-45e9-8017-109004c289ae)
![image](https://github.com/user-attachments/assets/6adcdc64-7a85-450d-8a9c-573ab131b08c)
![image](https://github.com/user-attachments/assets/198b8014-b843-4e45-b00f-d36ef89fa58f)
![image](https://github.com/user-attachments/assets/fb1f8bd8-5e1d-4073-bd9f-d875cd661f20)



