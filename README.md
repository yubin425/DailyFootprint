# 0. Getting Started (시작하기)
<img src="https://i.ibb.co/9vGRHYx/image-5.png"  alt="배너">
습관을 들이기는 참 어렵습니다. 기존의 습관 관리 애플리케이션을 사용한다고 해도 가짜로 입력하거나 넘겨버릴 수도 있고, 중도에 포기하기도 쉽습니다. 하루 발자국은 이러한 기존 문제점에서 착안해 개발을 시작했습니다.

<br/>
<br/>

# 1. Project Overview (프로젝트 개요)
- 프로젝트 이름: 하루발자국
- 프로젝트 설명: 위치 기반 습관 인증 애플리케이션

<br/>
<br/>

# 2. Team Members (팀원)
| 김규민 | 김선오 | 박윤배 | 서다영 | 이정윤 | 임유빈 
|:------:|:------:|:------:|:------:|:------:|:------:|

<br/>
<br/>

# 3. Key Features (주요 기능)
- **회원가입, 로그인**:
  - 구글 계정을 통해 회원가입하고 로그인합니다. 회원가입 시 DB에 유저정보가 등록됩니다.

- **위치 기반 인증**:
  - 해야 할 일 설정 시 인증 가능한 위치를 선택해 등록할 수 있습니다. 
    GPS 기능을 사용하여, 해당 장소 인근에 도착해야 어플에서 완료 버튼을 클릭할 수 있도록 합니다.

- **동기 유발 기능**:
  - 사용자가 연속적으로 일들을 수행했을 시에 다른 응원 문구를 출력하게 하고, 
    달력에 하루 단위로 수행 결과에 따라 다른 색상을 표시하여 달성한 목표치를 직관적으로 한눈에 볼 수 있도록 합니다.
  - 일 별로 목표치 달성 여부에 따라 이모지를 변화하거나, 위치 기반 인증을 했을 시 
    얼마나 많이 해당 장소에서 목표를 수행했는지 핀 등으로 표기해 주는 도구들을 통해 사용자가 꾸준히 일들을 수행할 수 있도록 동기를 유발하고 격려합니다.
- **친구 기능**:
  - 친구 등록을 통해 친구와의 챌린지 비교 기능을 제공합니다. 
    또한 친구의 챌린지 달성 여부를 실시간으로 볼 수 있고 과거의 기록 또한 한눈에 볼 수 있도록 제공합니다.

<br/>
<br/>

# 4. Technology Stack (기술 스택)
## 4.1 Language
|  |  |
|-----------------|-----------------|
| Kotlin    |

## 4.2 Backend
|  |  |  |
|-----------------|-----------------|-----------------|
| Firebase    |  <img src="https://github.com/user-attachments/assets/1694e458-9bb0-4a0b-8fe6-8efc6e675fa1" alt="Firebase" width="100">    | 10.12.5    |

<br/>

## 4.3 Cooperation
|  |  |
|-----------------|-----------------|
| Git    |  <img src="https://github.com/user-attachments/assets/483abc38-ed4d-487c-b43a-3963b33430e6" alt="git" width="100">    |
| Notion    |  <img src="https://github.com/user-attachments/assets/34141eb9-deca-416a-a83f-ff9543cc2f9a" alt="Notion" width="100">    |

<br/>

# 5. Project Structure (프로젝트 구조)
```plaintext
project/
├─.idea
│  └─codeStyles
├─app
│  └─src
│      ├─androidTest
│      │  └─java
│      │      └─com
│      │          └─example
│      │              └─dailyfootprint
│      ├─main
│      │  ├─java
│      │  │  └─com
│      │  │      └─example
│      │  │          └─dailyfootprint
│      │  │              ├─dao
│      │  │              ├─model
│      │  │              └─ui
│      │  │                  ├─challenge
│      │  │                  ├─dashboard
│      │  │                  ├─friendAlert
│      │  │                  ├─friends
│      │  │                  ├─home
│      │  │                  ├─login
│      │  │                  ├─Map
│      │  │                  └─mypage
│      │  └─res
│      │      ├─drawable
│      │      ├─layout
│      │      ├─menu
│      │      ├─mipmap-anydpi-v26
│      │      ├─mipmap-hdpi
│      │      ├─mipmap-mdpi
│      │      ├─mipmap-xhdpi
│      │      ├─mipmap-xxhdpi
│      │      ├─mipmap-xxxhdpi
│      │      ├─navigation
│      │      ├─values
│      │      ├─values-night
│      │      └─xml
│      └─test
│          └─java
│              └─com
│                  └─example
│                      └─dailyfootprint
└─gradle
    └─wrapper
```

<br/>
<br/>

# 6. Development Workflow (개발 워크플로우)
## 브랜치 전략 (Branch Strategy)
다음과 같은 브랜치를 사용합니다.

- Main Branch
- {featurename} Branch

<br/>
<br/>
