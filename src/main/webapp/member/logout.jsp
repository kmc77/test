<%@ page contentType="text/html; charset=EUC-KR" pageEncoding="EUC-KR"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="EUC-KR">
<title>�α׾ƿ� ������</title>
</head>
<body>
		<% session.invalidate();%> //������ ��� �Ӽ��� ����
		<script>
			alert("�α׾ƿ� �Ǿ����ϴ�.");
			location.href = "templatetest.jsp"
		
		</script>
</body>
</html>