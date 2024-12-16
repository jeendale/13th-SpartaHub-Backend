# 13th-SpartaHub
내일배움캠프 Ch.2 대규모 AI 시스템 설계 프로젝트

----
### 📖 프로젝트 소개(SpartaHub) ###
![SPARTA](https://github.com/user-attachments/assets/a83e87eb-24ef-4a42-a69c-96d72b33decc)

### MSA기반의 물류 관리 및 배송 시스템 ###
- #### 프로젝트 목표 ####
  - 흔히 접하던 모놀리식 구조가 아닌 MSA기반의 시스템을 설계하고 구현하면서, 다양한 기술과 방법론을 적용
  - 데이터 일관성을 유지하기 위한 트랜잭션 도입
  - GeminiAPI를 연동하여 AI 기능 구현
- #### 프로젝트 상세 ####
  - MASTER, HUB_MANAGER, SHIPMENT_MANAGER, COMPANY_MANAGER의 권한으로 관리하여 진행
  - 사용자, 업체, 허브, 상품, 주문, 배송 도메인으로 구성
  - 모든 테이블에 Audit 필드 추가하여 데이터 감사로그 기록(논리삭제(is_deleted) 컬럼 사용)
  - 개발기간 : 24.12.5 ~ 
----
### 👩‍💻 팀원 역할 분담 ###
 - **신민철 :** 팀장
 - **지현구 :**
 - **임지은 :** 
----
### 🔧 개발환경 ###
- **Framework  :** Spring Boot 3.4.0
- Java 17
- **Build Tool :** Gradle
- **IDE :** IntelliJ
- **ORM :** JPA
- **VCS :** GitHub(Forking Workflow 전략)
----
### ⚙️ 기술스택 ###
- **Server :** 
- **Database :** PostgreSQL, Redis
- **Storage :** 
- **API documentation:** Swagger
----
### 📝 ERD ###


----
### 📚 프로젝트 아키텍처 ###


----
### 🖥️ API ###
- **API 명세:** 
- **Swagger:**
- 
----
### ⚙️서비스 구성 및 실행 방법 ###


