<%@ page language="java" contentType="text/html; charset=EUC-KR" 
		pageEncoding="EUC-KR"%>
		
<!DOCTYPE html>
<html>
<head>
<title>�α��� ó��</title>
<link href="css/join.css" type="text/css" rel ="stylesheet">
<script src="js/jquery-3.7.0.js"></script>
<script>
	$(function() {
		let checkid = false;
		let checkemail = false;
		
		$("input[name = id]").on('keyup',
				function() {
			
			//[A-Za-z0-9_]�� �ǹ̰� \w
			const pattern = /^\w{5,12}$/;
			const id = $("input:eq(0)").val();
			if (!pattern.test(id)) {
				$("#message").css('color', 'red')
							 .html("������ ���� _�� 5 ~ 12�� �����մϴ�.");
				checkid = false;
				return;
			}
			
			$.ajax({
				url : "idcheck.net",
				 data: { id: id },  
		            success: function(resp) {
					if(resp == -1) {//db�� �ش� id �� ���� ���
						$("#message").css('color', 'green').html("��� ������ ���̵� �Դϴ�.");
						checkid = true;
					} else { //db�� �ش� id �� �ִ� ���(0)
					$("#message").css('color', 'blue').html("������� ���̵� �Դϴ�.");
					checkid = false;
					}
				}
			}); //ajax end
		}); //keyup end
	
	$("input[name=email]").on('keyup',
			function() {
				//[A-Za-z0-9_]�� ������ ���� \w �Դϴ�.
				//+�� 1ȸ �̻� �ݺ��� �ǹ��ϰ� {1,}�� �����մϴ�.
				//\w+ �� [AZa-z0-9_]�� 1�� �̻� ����϶�� �ǹ��Դϴ�.
				const pattern = /^\w+@\w+[.]\w{3}$/;
				const email_value = $(this).val();
				
				if(!pattern.test(email_value)) {
					$("#email_message").css('color', 'red')
									   .html("�̸��������� ���� �ʽ��ϴ�.")
					checkemail = false;				   
				} else {
					$("#email_message").css('color', 'green')
									   .html("�̸������Ŀ� �½��ϴ�.")
					checkemail = true;				   
				}
	}); //email keyup �̺�Ʈ ó�� ��

	$('form').submit(function() {
		if (!$.isNumeric($("input[name='age']").val())) {
			alert("���̴� ���ڸ� �Է��ϼ���");
			$("input[name='age']").val('').focus();
			return false;
		}
		
		if(!checkid) {
			alert("��밡���� id�� �Է��ϼ���.");
			$("input[name=id]").val('').focus();
			return false;
		}
		
		if(!checkemail){
			alert("email ������ Ȯ���ϼ���.");
			$("input[name=email]").focus();
			return false;
		}
	}); //submit
	});//ready
</script>
</head>
<body>
	<form name="joinform" action="joinProcess.net" method="post">

 <h1>ȸ������ </h1>
 	 <hr>
 	 <b>���̵�</b>
		<input type="text" name="id" placeholder="Enter id" maxLength="12" required>
		<span id="message"></span>
		
		<b>��й�ȣ</b>
		<input type="password" name="pass" placeholder="Enter password" required>
		
		<b>�̸�</b>
		<input type="text" name="name" placeholder="Enter name" maxLength="5" required>
		
		<b>����</b>
		<input type="text" name="age"  maxLength="2" placeholder="Enter age" required>
		
		<b>����</b>
		<div>
			<input type="radio" name="gender" value="��" checked><span>����</span>
			<input type="radio" name="gender" value="��"><span>����</span>
		</div>
		
		<b>�̸��� �ּ�</b>
			<input type="text" name="email" placeholder="Enter name" maxLength="30" required>
			<span id="email_message"></span>
			<div class="clearfix">
				<button type="submit" class="submitbtn">ȸ������</button>
				<button type="reset" class="cancelbtn">�ٽ��ۼ�</button>
			</div>
</form>
</body>
</html>