<%@ page contentType="text/html; charset=EUC-KR" pageEncoding="EUC-KR"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="EUC-KR">
<title>error.jsp</title>
<style>
	body{width:500px; margin:3em auto}
	div{
		color:orange;
		font-size:30px;
		text-align:center;
	}
	span{font-size:1.5rem; color:#5d5de2}
</style>
</head>
<body>
		<div>
			<p><img src="${pageContext.request.contextPath}/image/error.png" width="100px"></p>
			<p> �˼��մϴ�. <br>
				${message}</p>
			<span>���� �̿뿡 ������ ��� �˼��մϴ�.</span>
		</div>
</body>
</html>