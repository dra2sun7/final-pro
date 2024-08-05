# Kubernetes Cluster 보안 Scanner

Scanner를 위한 Backend에 대한 기본 코드입니다.

## 주요 기능

- **기능 1**: FrontEnd 페이지 주소인 http://www.kcs2.co.kr 의 요청만 허용합니다.
- **기능 2**: 전달받은 apiServer에 대한 주소와 해당하는 token값을 통해서 Kubernetes Clinet에 접속합니다.
- **기능 3**: 각각의 노드에 접속해 kube-bench를 Job으로 생성해 서버의 보안 상태를 점검합니다.

### 사전 요구 사항

- [필요한 소프트웨어 또는 도구] Java 19.0.1 이상
