<%@ page contentType="text/html; charset=EUC-KR" pageEncoding="EUC-KR"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="EUC-KR">
<title>로그아웃 페이지</title>
</head>
<body>
		<% session.invalidate();%> //세션의 모든 속성을 삭제
		<script>
			alert("로그아웃 되었습니다.");
			location.href = "templatetest.jsp"
		
		</script>
</body>
</html>